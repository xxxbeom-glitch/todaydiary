package com.todaydiary.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
                    var dataError by remember { mutableStateOf<String?>(null) }
                    val uiError = dataError ?: authError

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
                    // Firestore/로컬 임시저장에서 사용할 문서 ID(새 글은 + 누를 때 생성)
                    var activeEntryId by remember { mutableStateOf("") }
                    // '새로 쓰기(+)'는 기존 오늘짜 임시저장(draft)을 열지 않고 빈 화면부터 시작
                    var forceNewBlank by remember { mutableStateOf(false) }
                    // Compose TextFieldValue 상태가 initialBody(빈값)로 동일해 남는 케이스를 방지하기 위한 키
                    var editorResetKey by remember { mutableIntStateOf(0) }
                    var moreTarget: DiaryEntry? by remember { mutableStateOf(null) }
                    var showMore by remember { mutableStateOf(false) }
                    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
                    var showMonthPicker by remember { mutableStateOf(false) }
                    val repo = remember { FirestoreDiaryRepository() }
                    var cloudEntries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }
                    val draftStore = remember(context) { DraftStore(context) }
                    var draftRevision by remember { mutableIntStateOf(0) }

                    DisposableEffect(firebaseUser?.uid) {
                        val uid = firebaseUser?.uid
                        if (uid.isNullOrBlank()) {
                            cloudEntries = emptyList()
                            onDispose { }
                        } else {
                            val reg = repo.listenEntries(
                                uid = uid,
                                onUpdate = { list -> cloudEntries = list },
                                onError = { e -> dataError = "데이터 로드 실패: ${e.message ?: e.javaClass.simpleName}" },
                            )
                            onDispose { reg.remove() }
                        }
                    }

                    val mergedEntries = remember(cloudEntries, draftRevision) {
                        mergeDiaryEntries(cloud = cloudEntries, drafts = draftStore.listDraftEntries())
                    }
                    val listEntries = remember(mergedEntries, currentMonth) {
                        mergedEntries
                            .filter { YearMonth.from(it.date) == currentMonth }
                            .sortedByDescending { it.date }
                    }

                    when (screen) {
                        "editor" -> {
                            val editorDate = selectedEntry?.date ?: LocalDate.now()
                            val editorBody = when {
                                selectedEntry != null -> selectedEntry?.body ?: ""
                                forceNewBlank -> ""
                                else -> draftStore.loadDraftForEntry(activeEntryId, editorDate)
                            }
                            key(editorResetKey) {
                                DiaryEditorScreen(
                                    onBack = {
                                        forceNewBlank = false
                                        screen = "list"
                                    },
                                    initialDate = editorDate,
                                    initialBody = editorBody,
                                    onAutoSave = { date, body ->
                                        // 1) 로컬(기기) 우선 저장
                                        val entryId = (selectedEntry?.id?.ifBlank { null } ?: activeEntryId.ifBlank { null }).orEmpty()
                                        draftStore.saveDraft(entryId, date, body)
                                        draftRevision++
                                        // 2) 로그인된 경우에만 클라우드 백업(Firestore)
                                        val uid = firebaseUser?.uid
                                        if (!uid.isNullOrBlank()) {
                                            val idForCloud = (selectedEntry?.id?.ifBlank { null } ?: activeEntryId.ifBlank { null }).orEmpty()
                                            if (idForCloud.isBlank()) {
                                                // 안전장치: id 없이 저장하면(레거시 경로) 또 하루 1문서로 덮어쓰기 됨
                                                dataError = "내부 오류: 일기 ID가 없습니다. 앱을 다시 실행해보세요."
                                            } else {
                                                repo.saveEntry(
                                                    uid = uid,
                                                    entry = DiaryEntry(id = idForCloud, date = date, body = body),
                                                    onFailure = { e ->
                                                        dataError = "Firestore 저장 실패: ${e.message ?: e.javaClass.simpleName}"
                                                    },
                                                )
                                            }
                                        }
                                    },
                                )
                            }
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
                                entries = listEntries,
                                monthTitle = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                                onClickMonth = { showMonthPicker = true },
                                onClickCreate = {
                                    // 새로 작성: 직전에 열람/수정하던 entry 상태가 남으면
                                    // 동일 initial로 "수정"처럼 열리는 문제가 생깁니다.
                                    selectedEntry = null
                                    activeEntryId = repo.newEntryId()
                                    forceNewBlank = true
                                    // BasicTextField(TextFieldValue) 내부 상태가 initialBody(빈 값)로 동일할 때
                                    // 이전 화면 입력이 남는 문제를 방지
                                    editorResetKey++
                                    screen = "editor"
                                },
                                onClickHeaderLeft = { screen = "settings" },
                                onClickEntry = { entry ->
                                    forceNewBlank = false
                                    activeEntryId = entry.id
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
                        val available = mergedEntries
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
                                forceNewBlank = false
                                activeEntryId = moreTarget?.id.orEmpty()
                                selectedEntry = moreTarget
                                screen = "editor"
                            },
                            onDelete = {
                                val target = moreTarget
                                if (target != null) {
                                    val uid = firebaseUser?.uid
                                    if (!uid.isNullOrBlank()) {
                                        repo.deleteEntry(
                                            uid = uid,
                                            id = target.id,
                                            date = target.date,
                                            onFailure = { e ->
                                                dataError = "Firestore 삭제 실패: ${e.message ?: e.javaClass.simpleName}"
                                            },
                                        )
                                        draftStore.clearDraft(target.id, target.date)
                                        draftRevision++
                                    } else {
                                        draftStore.clearDraft(target.id, target.date)
                                        draftRevision++
                                    }
                                    if (selectedEntry == target) selectedEntry = null
                                }
                                if (screen == "view") {
                                    screen = "list"
                                }
                            },
                        )
                    }

                    if (uiError != null) {
                        AlertDialog(
                            onDismissRequest = {
                                dataError = null
                                authError = null
                            },
                            text = { androidx.compose.material3.Text(text = uiError ?: "") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        dataError = null
                                        authError = null
                                    }
                                ) {
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

private fun mergeDiaryEntries(cloud: List<DiaryEntry>, drafts: List<DiaryEntry>): List<DiaryEntry> {
    fun key(e: DiaryEntry): String {
        if (e.id.isNotBlank()) return e.id
        // 레거시(또는 id 없는 로컬) 병합 키: 날짜 + 본문 해시로 충돌을 최대한 줄임
        return "legacy|${e.date}|${e.body.hashCode()}"
    }

    val map = LinkedHashMap<String, DiaryEntry>()
    for (e in cloud) {
        map[key(e)] = e
    }
    for (d in drafts) {
        val k = key(d)
        val existing = map[k]
        map[k] = if (existing == null) {
            d
        } else {
            if (d.body.length >= existing.body.length) d else existing
        }
    }
    return map.values.sortedWith(
        compareByDescending<DiaryEntry> { it.date }
            .thenByDescending { it.id }
    )
}
