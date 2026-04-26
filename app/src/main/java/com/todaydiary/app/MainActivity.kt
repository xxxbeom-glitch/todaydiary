package com.todaydiary.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.todaydiary.app.data.DraftStore
import com.todaydiary.app.data.FirestoreDiaryRepository
import com.todaydiary.app.ui.DiaryEditorScreen
import com.todaydiary.app.ui.DiaryListScreen
import com.todaydiary.app.ui.DiaryViewScreen
import com.todaydiary.app.ui.SettingsScreen
import com.todaydiary.app.ui.components.DiaryMoreDialog
import com.todaydiary.app.ui.components.MonthDrumRollDialog
import com.todaydiary.app.auth.rememberFirebaseUserState
import com.todaydiary.app.ui.theme.TodayDiaryTheme
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate
import java.time.YearMonth
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density = density.density, fontScale = 1.0f)
            ) {
                TodayDiaryTheme {
                    val context = LocalContext.current
                    val firebaseUser by rememberFirebaseUserState()
                    val auth = remember { FirebaseAuth.getInstance() }

                    val gso = remember {
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                    }
                    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

                    var authError by remember { mutableStateOf<String?>(null) }

                    val signInLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                            val account = task.getResult(ApiException::class.java)
                            val idToken = account.idToken
                            if (idToken.isNullOrBlank()) {
                                authError = "Google 로그인 토큰을 받지 못했습니다. Firebase 콘솔의 OAuth/패키지명/SHA 설정을 확인해주세요."
                                return@rememberLauncherForActivityResult
                            }
                            val credential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(credential)
                                .addOnFailureListener { e ->
                                    authError = "Firebase 로그인 실패: ${e.message ?: e.javaClass.simpleName}"
                                }
                        } catch (e: ApiException) {
                            authError = "Google 로그인 실패(code=${e.statusCode}): ${e.message ?: ""}"
                        } catch (e: Exception) {
                            authError = "로그인 처리 중 오류: ${e.message ?: e.javaClass.simpleName}"
                        }
                    }

                    var screen by remember { mutableStateOf("list") }
                    var selectedEntry: DiaryEntry? by remember { mutableStateOf(null) }
                    var moreTarget: DiaryEntry? by remember { mutableStateOf(null) }
                    var showMore by remember { mutableStateOf(false) }
                    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
                    var showMonthPicker by remember { mutableStateOf(false) }
                    val repo = remember { FirestoreDiaryRepository() }
                    var entries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }
                    val draftStore = remember(context) { DraftStore(context) }

                    DisposableEffect(firebaseUser?.uid) {
                        val uid = firebaseUser?.uid
                        if (uid.isNullOrBlank()) {
                            entries = emptyList()
                            onDispose { }
                        } else {
                            val reg = repo.listenEntries(
                                uid = uid,
                                onUpdate = { list -> entries = list },
                                onError = { e -> authError = "데이터 로드 실패: ${e.message ?: e.javaClass.simpleName}" },
                            )
                            onDispose { reg.remove() }
                        }
                    }

                    when (screen) {
                        "editor" -> {
                            val editorDate = selectedEntry?.date ?: LocalDate.now()
                            val editorBody = if (selectedEntry != null) {
                                selectedEntry?.body ?: ""
                            } else {
                                draftStore.loadDraft(editorDate)
                            }
                            DiaryEditorScreen(
                                onBack = { screen = "list" },
                                initialDate = editorDate,
                                initialBody = editorBody,
                                onAutoSave = { date, body ->
                                    // 1) 로컬(기기) 우선 저장
                                    draftStore.saveDraft(date, body)
                                    // 2) 로그인된 경우에만 클라우드 백업(Firestore)
                                    val uid = firebaseUser?.uid
                                    if (!uid.isNullOrBlank()) {
                                        repo.saveEntry(uid, DiaryEntry(date = date, body = body))
                                    }
                                },
                            )
                        }
                        "view" -> {
                            DiaryViewScreen(
                                entry = selectedEntry ?: DiaryEntry(date = java.time.LocalDate.now(), body = ""),
                                onBack = { screen = "list" },
                                onMore = {
                                    moreTarget = selectedEntry
                                    showMore = true
                                },
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                onBack = { screen = "list" },
                                isAccountLinked = firebaseUser != null,
                                linkedEmail = firebaseUser?.email,
                                onClickLogin = {
                                    signInLauncher.launch(googleSignInClient.signInIntent)
                                },
                                onClickLogout = {
                                    auth.signOut()
                                    googleSignInClient.signOut()
                                },
                            )
                        }
                        else -> {
                            DiaryListScreen(
                                entries = entries,
                                monthTitle = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                                onClickMonth = { showMonthPicker = true },
                                onClickCreate = { screen = "editor" },
                                onClickHeaderLeft = { screen = "settings" },
                                onClickEntry = { entry ->
                                    selectedEntry = entry
                                    screen = "view"
                                },
                                onClickEntryMore = { entry ->
                                    moreTarget = entry
                                    showMore = true
                                },
                            )
                        }
                    }

                    if (showMonthPicker) {
                        val nowYm = YearMonth.now()
                        val available = entries
                            .map { YearMonth.from(it.date) }
                            .distinct()
                            .sorted()
                            .let { months ->
                                // 데이터가 현재월 1개뿐이면 그 1개만 보여줌 (이전달/이전월 노출 X)
                                if (months.isEmpty()) listOf(nowYm) else months
                            }
                        MonthDrumRollDialog(
                            initial = currentMonth,
                            availableMonths = available,
                            onDismiss = { showMonthPicker = false },
                            onPicked = { picked -> currentMonth = picked },
                        )
                    }

                    if (showMore && moreTarget != null) {
                        DiaryMoreDialog(
                            onDismiss = { showMore = false },
                            onEdit = {
                                selectedEntry = moreTarget
                                screen = "editor"
                            },
                            onDelete = {
                                val target = moreTarget
                                if (target != null) {
                                    val uid = firebaseUser?.uid
                                    if (!uid.isNullOrBlank()) {
                                        repo.deleteEntry(uid, target.date)
                                    } else {
                                        entries = entries.filterNot { it == target }
                                    }
                                    if (selectedEntry == target) selectedEntry = null
                                }
                                if (screen == "view") {
                                    screen = "list"
                                }
                            },
                        )
                    }

                    if (authError != null) {
                        AlertDialog(
                            onDismissRequest = { authError = null },
                            text = { androidx.compose.material3.Text(text = authError ?: "") },
                            confirmButton = {
                                TextButton(onClick = { authError = null }) {
                                    androidx.compose.material3.Text("확인")
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
