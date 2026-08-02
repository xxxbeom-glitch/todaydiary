# Debug Screen Catalog Guide

## 목적

하루기록(Today Diary)의 화면을 실제 앱 메뉴, 서버, DB, 로그인, Navigation 연결 전에도 에뮬레이터나 기기에서 하나씩 직접 확인하기 위한 Debug 전용 화면 모음이다.

쉽게 말하면 다음과 같다.

```text
Today Diary UI Catalog
├─ 온보딩
├─ 홈
├─ 운동
├─ 루틴
├─ 운동 진행
├─ 기록
└─ 통계
```

각 화면은 Content, Loading, Empty, Error처럼 실제로 필요한 상태를 선택해 직접 열 수 있어야 한다.

## 핵심 원칙

- Debug 빌드에서만 제공한다.
- 실제 앱 Navigation 연결 여부와 무관하게 화면을 직접 실행한다.
- 서버, DB, 로그인, Repository, ViewModel 없이 Fake UiState로 실행한다.
- 모든 화면을 무조건 등록하지 않고 사용자가 보는 주요 Screen을 등록한다.
- 새 주요 화면은 Preview와 Debug Catalog 등록까지 끝나야 구현 완료로 본다.

## 권장 구조

```text
app/src/
├─ main/java/.../
│  └─ feature/exercise/
│     ├─ ExerciseDetailRoute.kt
│     ├─ ExerciseDetailScreen.kt
│     └─ ExerciseDetailUiState.kt
│
└─ debug/
   ├─ AndroidManifest.xml
   └─ java/.../debugcatalog/
      ├─ DebugCatalogActivity.kt
      ├─ DebugCatalogScreen.kt
      ├─ DebugScreenEntry.kt
      ├─ DebugScreenRegistry.kt
      └─ fake/
         └─ ExerciseDetailFakeStates.kt
```

`main`에는 출시 앱에서도 사용하는 Screen과 UiState를 둔다. `debug`에는 Catalog, Registry, Fake 데이터만 둔다.

## 화면 코드 분리

```kotlin
@Composable
fun ExerciseDetailRoute(
    viewModel: ExerciseDetailViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ExerciseDetailScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

@Composable
fun ExerciseDetailScreen(
    uiState: ExerciseDetailUiState,
    onAction: (ExerciseDetailAction) -> Unit,
    onBack: () -> Unit,
) {
    // 전달받은 상태만 화면에 그린다.
}
```

Debug Catalog는 `ExerciseDetailRoute`가 아니라 `ExerciseDetailScreen`을 직접 실행한다.

## Entry 모델 예시

```kotlin
data class DebugScreenEntry(
    val id: String,
    val title: String,
    val category: String,
    val stateName: String,
    val description: String? = null,
    val content: @Composable () -> Unit,
)
```

## Registry 예시

```kotlin
val debugScreenRegistry = listOf(
    DebugScreenEntry(
        id = "EXERCISE_DETAIL",
        title = "운동 상세",
        category = "운동",
        stateName = "Content",
    ) {
        ExerciseDetailScreen(
            uiState = ExerciseDetailFakeStates.content,
            onAction = {},
            onBack = {},
        )
    },
    DebugScreenEntry(
        id = "EXERCISE_DETAIL",
        title = "운동 상세",
        category = "운동",
        stateName = "Error",
    ) {
        ExerciseDetailScreen(
            uiState = ExerciseDetailFakeStates.error,
            onAction = {},
            onBack = {},
        )
    },
)
```

## Catalog 화면 기본 기능

초기에는 아래만 구현한다.

- 카테고리별 화면 목록
- 화면명과 상태명 표시
- 항목을 눌러 전체 화면으로 실행
- 뒤로 가기
- 화면이 많아질 경우 간단한 검색

디바이스 크기 변경, 글자 크기 변경, 다크 모드 전환 같은 고급 기능은 실제 필요가 생길 때 추가한다.

## 실행 방식

권장 방식은 `src/debug/AndroidManifest.xml`에 Debug Catalog 전용 Activity를 등록해 Debug 빌드에서 별도 실행 아이콘으로 여는 것이다. Release Manifest에는 등록하지 않는다.

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <activity
            android:name=".debugcatalog.DebugCatalogActivity"
            android:exported="true"
            android:label="Today Diary UI Catalog">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

프로젝트 설정상 별도 Launcher가 불편하면 Debug 빌드의 개발자 메뉴에서 Catalog Activity로 진입해도 된다.

## 화면 상태 기준

각 화면에 실제로 필요한 상태만 등록한다.

```text
Content      정상 데이터
Loading      로딩이 존재하는 화면
Empty        데이터가 없을 수 있는 화면
Error        오류를 사용자에게 표시하는 화면
Long Content 긴 제목·설명·목록 검수
Disabled     비활성 입력이나 버튼이 있는 화면
```

## Figma Screen Map 연동

`agent/FIGMA_SCREEN_MAP.md`에 다음 정보를 기록한다.

```text
Screen ID
Figma Frame
Screen File
대표 상태
Debug Catalog 등록 상태
검증 상태
```

Screen ID는 Figma, 코드, Debug Catalog에서 동일하게 사용한다.

## 완료 기준

- Debug 빌드에서 Catalog를 실행할 수 있다.
- 목록에서 주요 화면을 찾을 수 있다.
- 실제 앱 링크 없이 각 Screen을 직접 열 수 있다.
- 최소 Content 상태가 Fake UiState로 실행된다.
- 필요한 주요 상태가 크래시 없이 실행된다.
- Catalog 구현이 `src/debug`에 있어 Release 앱에 포함되지 않는다.
