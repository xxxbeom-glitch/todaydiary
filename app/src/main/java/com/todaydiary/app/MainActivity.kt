package com.todaydiary.app

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.unit.Density
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.todaydiary.app.data.DiaryBodyDedupe
import com.todaydiary.app.data.DiaryPhotoStorage
import com.todaydiary.app.data.DraftStore
import com.todaydiary.app.data.FirestoreDiaryRepository
import com.todaydiary.app.data.FirestoreUserRepository
import com.todaydiary.app.data.ThemePreferenceStore
import com.todaydiary.app.ui.DiaryEditorScreen
import com.todaydiary.app.ui.DiaryListScreen
import com.todaydiary.app.ui.DiaryViewScreen
import com.todaydiary.app.ui.SettingsScreen
import com.todaydiary.app.ui.components.DiaryMoreDialog
import com.todaydiary.app.ui.components.MonthDrumRollDialog
import com.todaydiary.app.auth.rememberFirebaseUserState
import com.todaydiary.app.ui.theme.TodayDiaryTheme
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

/**
 * 일기 목록「보는 달」을 실제 달이 넘어갔을 때 자동으로 맞춤.
 * - 백그라운드: 멈출 때 보던 달이 당시 달력과 같았다면(실시간 일기 중), 돌아올 때 오늘 달로 이동.
 * - 과거 월만 보는 중(pause 시점에 이미 달력과 다름)이면 건드리지 않음.
 * - 포그라운드에서 한 달만 넘어간 경우: 직전 달 → 다음 달 한 칸 이동.
 */
/** Firestore 리스너가 짧은 간격으로 여러 번 호출될 때(캐시→서버 등) 목록이 한 줄씩 늘어나 보이지 않게 묶음 */
private class CloudSnapshotCoalesce {
    var firstEventAtElapsed: Long = 0L
    var job: Job? = null
    var pending: List<DiaryEntry> = emptyList()
}

private fun computeAutoAdvanceMonth(
    pausedListMonth: String?,
    pausedWallMonth: String?,
    currentMonthString: String,
): YearMonth? {
    val nowYm = YearMonth.now()
    val cur = runCatching { YearMonth.parse(currentMonthString) }.getOrElse { nowYm }
    val pList = pausedListMonth?.let { runCatching { YearMonth.parse(it) }.getOrNull() }
    val pWall = pausedWallMonth?.let { runCatching { YearMonth.parse(it) }.getOrNull() }
    if (pList != null && pWall != null && pList == pWall && nowYm > pWall) {
        return nowYm
    }
    if (cur < nowYm && cur.plusMonths(1) == nowYm) {
        return nowYm
    }
    return null
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themeStore = remember { ThemePreferenceStore(context.applicationContext) }
            var screenThemeIndex by remember { mutableIntStateOf(if (themeStore.isDarkModeEnabled()) 1 else 0) }
            var fontIndex by remember { mutableIntStateOf(themeStore.getFontIndex()) }
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density = density.density, fontScale = 1.0f)
            ) {
                TodayDiaryTheme(
                    darkTheme = screenThemeIndex == 1,
                    fontIndex = fontIndex,
                ) {
                    val firebaseUser by rememberFirebaseUserState()
                    val auth = remember { FirebaseAuth.getInstance() }
                    val userRepo = remember { FirestoreUserRepository() }

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
                    val coroutineScope = rememberCoroutineScope()
                    /** 에디터에서 Firestore 저장/삭제가 겹치면(지운 뒤 이전 저장이 늦게 완료) 목록에 유령 본문이 남음 */
                    val editorCloudMutex = remember { Mutex() }
                    var selectedEntry: DiaryEntry? by remember { mutableStateOf(null) }
                    // Firestore/로컬 임시저장에서 사용할 문서 ID(새 글은 + 누를 때 생성)
                    var activeEntryId by remember { mutableStateOf("") }
                    // '새로 쓰기(+)'는 기존 오늘짜 임시저장(draft)을 열지 않고 빈 화면부터 시작
                    var forceNewBlank by remember { mutableStateOf(false) }
                    // Compose TextFieldValue 상태가 initialBody(빈값)로 동일해 남는 케이스를 방지하기 위한 키
                    var editorResetKey by remember { mutableIntStateOf(0) }
                    var moreTarget: DiaryEntry? by remember { mutableStateOf(null) }
                    var showMore by remember { mutableStateOf(false) }
                    /** 목록「보는 달」— [rememberSaveable]로 Process death·회전 등 Activity 재생성 뒤에도 유지(미저장이면 [YearMonth.now]로 떨어짐). */
                    var currentMonthString by rememberSaveable { mutableStateOf(YearMonth.now().toString()) }
                    val currentMonth: YearMonth = runCatching { YearMonth.parse(currentMonthString) }
                        .getOrElse { YearMonth.now() }
                    var showMonthPicker by remember { mutableStateOf(false) }
                    /** 휠 다이얼로그: 열 때 [currentMonth]만 쓰고, 스크롤 중 onPicked로 `initial`을 갱신하지 않음(휠 리셋 방지). */
                    var monthPickerInitial by remember { mutableStateOf(YearMonth.now()) }
                    /** 다이얼로그를 열 때마다 +1. [MonthDrumRollDialog] 내부 [remember]가 `years`/`availableMonths` 갱신으로 휠이 초기화되지 않게 씀. */
                    var monthPickerOpenSession by remember { mutableIntStateOf(0) }
                    /** [ON_STOP] 때의 목록 달 / 당시 달력 달 — 실시간 일기 vs 과거 보관 구분용 */
                    var pausedListMonth by rememberSaveable { mutableStateOf<String?>(null) }
                    var pausedWallMonth by rememberSaveable { mutableStateOf<String?>(null) }
                    val repo = remember { FirestoreDiaryRepository() }
                    /** 마이그레이션·동기화용: 리스너가 주는 최신 목록(지연 없음) */
                    var cloudEntries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }
                    /** 목록 UI 병합용: 초기 연속 스냅샷만 짧게 묶어 '1건→전체' 번쩍임 완화 */
                    var cloudEntriesForUi by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }
                    val cloudCoalesce = remember(firebaseUser?.uid) { CloudSnapshotCoalesce() }
                    // Firestore diaries 첫 스냅샷이 도착했는지(로딩/빈 컬렉션 구분용)
                    var cloudDataReady by remember { mutableStateOf(false) }
                    // 로그인 세션(uid)마다 1회: 기기에만 남은 레거시(날짜키) 드래프트를 Firestore로 백업
                    var didMigrateLocalDrafts by remember { mutableStateOf(false) }
                    val draftStore = remember(context) { DraftStore(context) }
                    var draftRevision by remember { mutableIntStateOf(0) }

                    val lifecycleOwner = LocalLifecycleOwner.current
                    val currentMonthStrForLifecycle = rememberUpdatedState(currentMonthString)
                    DisposableEffect(lifecycleOwner) {
                        val observer = LifecycleEventObserver { _, event ->
                            when (event) {
                                Lifecycle.Event.ON_STOP -> {
                                    pausedListMonth = currentMonthStrForLifecycle.value
                                    pausedWallMonth = YearMonth.now().toString()
                                }
                                Lifecycle.Event.ON_RESUME -> {
                                    computeAutoAdvanceMonth(
                                        pausedListMonth = pausedListMonth,
                                        pausedWallMonth = pausedWallMonth,
                                        currentMonthString = currentMonthStrForLifecycle.value,
                                    )?.let { ym -> currentMonthString = ym.toString() }
                                }
                                else -> Unit
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                    }

                    val screenRef = rememberUpdatedState(screen)
                    val monthStrRef = rememberUpdatedState(currentMonthString)
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(45_000L)
                            if (screenRef.value != "list") continue
                            computeAutoAdvanceMonth(
                                pausedListMonth = null,
                                pausedWallMonth = null,
                                currentMonthString = monthStrRef.value,
                            )?.let { ym ->
                                currentMonthString = ym.toString()
                            }
                        }
                    }

                    LaunchedEffect(firebaseUser?.uid) {
                        val u = firebaseUser
                        if (u != null) {
                            userRepo.ensureUserDocument(
                                user = u,
                                onFailure = { e ->
                                    dataError = "유저 문서 생성 실패: ${e.message ?: e.javaClass.simpleName}"
                                },
                            )
                        }
                    }

                    LaunchedEffect(firebaseUser?.uid) {
                        // uid 바뀔 때마다 마이그레이션/클라우드 준비 플래그를 초기화
                        cloudDataReady = false
                        didMigrateLocalDrafts = false
                    }

                    DisposableEffect(firebaseUser?.uid) {
                        val uid = firebaseUser?.uid
                        if (uid.isNullOrBlank()) {
                            cloudCoalesce.job?.cancel()
                            cloudEntries = emptyList()
                            cloudEntriesForUi = emptyList()
                            cloudDataReady = false
                            onDispose { }
                        } else {
                            val reg = repo.listenEntries(
                                uid = uid,
                                onUpdate = { list ->
                                    cloudDataReady = true
                                    cloudEntries = list
                                    cloudCoalesce.pending = list
                                    val now = SystemClock.elapsedRealtime()
                                    if (cloudCoalesce.firstEventAtElapsed == 0L) {
                                        cloudCoalesce.firstEventAtElapsed = now
                                    }
                                    val inBurst = now - cloudCoalesce.firstEventAtElapsed < 280L
                                    if (inBurst) {
                                        cloudCoalesce.job?.cancel()
                                        cloudCoalesce.job = coroutineScope.launch {
                                            delay(72L)
                                            cloudEntriesForUi = cloudCoalesce.pending
                                        }
                                    } else {
                                        cloudCoalesce.job?.cancel()
                                        cloudEntriesForUi = list
                                    }
                                },
                                onError = { e -> dataError = "데이터 로드 실패: ${e.message ?: e.javaClass.simpleName}" },
                            )
                            onDispose {
                                cloudCoalesce.job?.cancel()
                                reg.remove()
                            }
                        }
                    }

                    LaunchedEffect(firebaseUser?.uid, cloudDataReady) {
                        val uid = firebaseUser?.uid
                        if (uid.isNullOrBlank()) return@LaunchedEffect
                        if (!cloudDataReady) return@LaunchedEffect
                        if (didMigrateLocalDrafts) return@LaunchedEffect

                        val localOnly = draftStore.listDraftEntries().filter { it.id.isBlank() }
                        if (localOnly.isEmpty()) {
                            didMigrateLocalDrafts = true
                            return@LaunchedEffect
                        }

                        for (d in localOnly) {
                            val existsSameDay = cloudEntries.any { it.date == d.date }
                            if (existsSameDay) {
                                // 이미 클라우드에 해당 날짜 문서가 있으면(백업됨) 레거시 날짜키 드래프트는 제거
                                draftStore.clearDraft(d.date)
                                continue
                            }

                            val newId = repo.newEntryId()
                            try {
                                // 1) 클라우드 백업 먼저(성공한 뒤에 로컬 키를 legacy -> id 기반으로 옮김)
                                repo.saveEntry(
                                    uid = uid,
                                    entry = DiaryEntry(id = newId, date = d.date, body = d.body),
                                ).await()
                                draftStore.saveDraft(newId, d.date, d.body)
                                draftStore.clearDraft(d.date) // legacy key 제거
                                draftRevision++
                            } catch (e: Exception) {
                                if (e is CancellationException) throw e
                                if (e.message?.contains("left the composition", ignoreCase = true) == true) {
                                    return@LaunchedEffect
                                }
                                dataError = "로컬 일기 백업 실패: ${e.message ?: e.javaClass.simpleName}"
                                // 실패 시에는 didMigrateLocalDrafts를 true로 올리지 않아 재시도 가능
                                return@LaunchedEffect
                            }
                        }

                        didMigrateLocalDrafts = true
                    }

                    // "날짜키"가 아닌 UUID 기기 전용 드래프트 — 클라에 없으면 업로드.
                    // cloudEntries를 LaunchedEffect 키에 넣지 않음: 첫 save 후 스냅샷이 바뀔 때마다 효과가 취소되어
                    // .await() 가 "The coroutine scope left the composition" 으로 실패할 수 있음.
                    LaunchedEffect(firebaseUser?.uid, cloudDataReady) {
                        val uid = firebaseUser?.uid
                        if (uid.isNullOrBlank()) return@LaunchedEffect
                        if (!cloudDataReady) return@LaunchedEffect
                        val alreadyInCloud = cloudEntries.mapNotNull { it.id.takeIf { s -> s.isNotBlank() } }.toMutableSet()
                        for (d in draftStore.listDraftEntries()) {
                            if (d.id.isBlank() || d.body.isBlank()) continue
                            if (d.id in alreadyInCloud) continue
                            try {
                                repo.saveEntry(
                                    uid = uid,
                                    entry = d,
                                ).await()
                                alreadyInCloud.add(d.id)
                            } catch (e: Exception) {
                                if (e is CancellationException) throw e
                                if (e.message?.contains("left the composition", ignoreCase = true) == true) {
                                    return@LaunchedEffect
                                }
                                dataError = "로컬 일기 클라우드 동기화 실패: ${e.message ?: e.javaClass.simpleName}"
                                return@LaunchedEffect
                            }
                        }
                    }

                    val mergedEntries = remember(cloudEntriesForUi, draftRevision) {
                        mergeDiaryEntries(cloud = cloudEntriesForUi, drafts = draftStore.listDraftEntries())
                    }
                    val listEntries = remember(mergedEntries, currentMonth) {
                        mergedEntries
                            .filter { YearMonth.from(it.date) == currentMonth }
                            .sortedWith(
                                compareByDescending<DiaryEntry> { it.date }
                                    .thenByDescending { it.id }
                            )
                    }

                    // 휠은 mergedEntries에 있는 달만 쓰면, 일기가 없는 달로만 이동한 경우
                    // initial 연도가 years에 없어 index=0(다른 해)으로 떨어지며 currentMonth가 '최신'으로 덮인다.
                    // 보고 있는 달은 항시 후보에 넣는다(리스트 참조는 remember로 안정화).
                    val monthPickerAvailable = remember(mergedEntries, currentMonth) {
                        val fromData = mergedEntries.map { YearMonth.from(it.date) }.toMutableSet()
                        fromData.add(currentMonth)
                        fromData.add(YearMonth.now())
                        fromData.sorted()
                    }

                    // 시스템/제스처 뒤로가기: 대화/피커 먼저 닫기, 그다음 list가 아닌 화면만 list로(루트에선 앱 종료)
                    val shouldInterceptBack = (showMore && moreTarget != null) ||
                        showMonthPicker || uiError != null || screen in setOf("view", "settings")
                    BackHandler(enabled = shouldInterceptBack) {
                        when {
                            showMore && moreTarget != null -> showMore = false
                            showMonthPicker -> showMonthPicker = false
                            uiError != null -> {
                                dataError = null
                                authError = null
                            }
                            screen == "view" -> screen = "list"
                            screen == "settings" -> screen = "list"
                        }
                    }

                    when (screen) {
                        "editor" -> {
                            val editorDate = selectedEntry?.date ?: LocalDate.now()
                            // 기기 드래프트가 있으면 항상 그걸 씀(+ 직후 forceNewBlank=true여도 본문이 잡힘). 그렇지 않으면
                            // selected(클라/목록) → 완전 신규 +만 빈 화면. 아니면 로그인/리컴포즈 시 initialBody가 ""로만 잡혀
                            // TextField가 remember로 초기화되며 '이원화'처럼 보이는 문제가 남.
                            val fromDraft = draftStore.loadDraftForEntry(activeEntryId, editorDate)
                            val editorBody = when {
                                fromDraft.isNotBlank() -> fromDraft
                                selectedEntry != null -> selectedEntry?.body ?: ""
                                forceNewBlank -> ""
                                else -> ""
                            }
                            key(editorResetKey) {
                                DiaryEditorScreen(
                                    onBack = {
                                        forceNewBlank = false
                                        screen = "list"
                                    },
                                    initialDate = editorDate,
                                    initialBody = editorBody,
                                    onAutoSave = { date, body, writtenAt: Instant ->
                                        if (body.isNotBlank() && forceNewBlank) {
                                            forceNewBlank = false
                                        }
                                        // 1) 로컬(기기) 우선 저장 (빈 본문이면 DraftStore가 초기화만 함)
                                        val entryId = (selectedEntry?.id?.ifBlank { null } ?: activeEntryId.ifBlank { null }).orEmpty()
                                        draftStore.saveDraft(entryId, date, body)
                                        draftRevision++

                                        val uid = firebaseUser?.uid
                                        val idForCloud = (selectedEntry?.id?.ifBlank { null } ?: activeEntryId.ifBlank { null }).orEmpty()
                                        val editingExisting = selectedEntry != null
                                        if (uid.isNullOrBlank()) {
                                            /* 로그인 없으면 클라우드 생략 */
                                        } else if (idForCloud.isBlank()) {
                                            if (body.isNotBlank()) {
                                                dataError = "내부 오류: 일기 ID가 없습니다. 앱을 다시 실행해보세요."
                                            }
                                        } else {
                                            coroutineScope.launch {
                                                editorCloudMutex.withLock {
                                                    if (body.isBlank()) {
                                                        // 새 글(+): 이미 올라간 초안이 있으면 제거. 기존 글 편집은 빈 저장으로 클라우드를 건드리지 않음(이전 본문 유지)
                                                        if (!editingExisting) {
                                                            withContext(Dispatchers.IO) {
                                                                runCatching {
                                                                    repo.deleteEntry(uid, idForCloud, date)
                                                                }
                                                            }
                                                        }
                                                        return@withLock
                                                    }
                                                    val photosForSave = selectedEntry
                                                        ?.takeIf { it.id == idForCloud }
                                                        ?.photos
                                                        ?: emptyList()
                                                    val photos = try {
                                                        DiaryPhotoStorage.ensurePhotosInRemoteStorage(
                                                            context,
                                                            uid,
                                                            idForCloud,
                                                            photosForSave,
                                                        )
                                                    } catch (e: Exception) {
                                                        if (e is CancellationException) throw e
                                                        dataError =
                                                            "사진 처리 실패: ${e.message ?: e.javaClass.simpleName}"
                                                        return@withLock
                                                    }
                                                    try {
                                                        withContext(Dispatchers.IO) {
                                                            repo.saveEntry(
                                                                uid = uid,
                                                                entry = DiaryEntry(
                                                                    id = idForCloud,
                                                                    date = date,
                                                                    body = body,
                                                                    photos = photos,
                                                                ),
                                                                writtenAt = writtenAt,
                                                            ).await()
                                                        }
                                                    } catch (e: Exception) {
                                                        if (e is CancellationException) throw e
                                                        dataError =
                                                            "Firestore 저장 실패: ${e.message ?: e.javaClass.simpleName}"
                                                    }
                                                }
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
                                screenThemeIndex = screenThemeIndex,
                                onScreenThemeIndexChange = { i ->
                                    screenThemeIndex = i
                                    themeStore.setDarkModeEnabled(i == 1)
                                },
                                fontIndex = fontIndex,
                                onFontIndexChange = { i ->
                                    fontIndex = i
                                    themeStore.setFontIndex(i)
                                },
                            )
                        }
                        else -> {
                            DiaryListScreen(
                                entries = listEntries,
                                monthTitle = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                                onClickMonth = {
                                    monthPickerInitial = currentMonth
                                    monthPickerOpenSession++
                                    showMonthPicker = true
                                },
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
                        val available = if (monthPickerAvailable.isEmpty()) {
                            listOf(YearMonth.now())
                        } else {
                            monthPickerAvailable
                        }
                        MonthDrumRollDialog(
                            openSession = monthPickerOpenSession,
                            initial = monthPickerInitial,
                            availableMonths = available,
                            onDismiss = { showMonthPicker = false },
                            onPicked = { picked -> currentMonthString = picked.toString() },
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
                                val fromView = screen == "view"
                                if (target != null) {
                                    val uid = firebaseUser?.uid
                                    if (!uid.isNullOrBlank()) {
                                        coroutineScope.launch {
                                            try {
                                                withContext(Dispatchers.IO) {
                                                    repo.deleteEntry(
                                                        uid = uid,
                                                        id = target.id,
                                                        date = target.date,
                                                    )
                                                }
                                                draftStore.clearDraft(target.id, target.date)
                                                draftRevision++
                                                if (selectedEntry == target) selectedEntry = null
                                                if (fromView) screen = "list"
                                            } catch (e: Exception) {
                                                if (e is CancellationException) throw e
                                                dataError = "Firestore 삭제 실패: ${e.message ?: e.javaClass.simpleName}"
                                            }
                                        }
                                    } else {
                                        draftStore.clearDraft(target.id, target.date)
                                        draftRevision++
                                        if (selectedEntry == target) selectedEntry = null
                                        if (fromView) screen = "list"
                                    }
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
                            text = { Text(text = uiError ?: "") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        dataError = null
                                        authError = null
                                    }
                                ) {
                                    Text("확인")
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
    // 동일 id가 아닌 **같은 날·같은 본문(정규화)** 은 1건만(불러오기 2회·서버/로컬 미세차이)
    return DiaryBodyDedupe.pickOnePerDuplicateKey(map.values.toList())
        .sortedWith(
            compareByDescending<DiaryEntry> { it.date }
                .thenByDescending { it.id }
        )
}
