---
name: register-debug-screen
description: >-
  Registers a Compose screen in the debug-only UI Catalog with Fake UiState so
  it can be opened without app navigation. Use when adding a major screen,
  state variants, or updating Debug Catalog entries.
---

# Register Debug Screen

새 Compose 화면을 앱 내부 Debug Catalog에서 직접 열 수 있도록 등록할 때 사용한다.

## Use when

- 새로운 주요 화면을 구현했을 때
- 실제 Navigation이 아직 연결되지 않은 화면을 기기에서 확인할 때
- Loading, Content, Empty, Error 등 상태별 화면을 비교할 때
- 기존 Debug Catalog의 목록이나 Fake UiState를 수정할 때

## Procedure

### 1. 독립 실행 가능 여부 확인

- 화면이 `Screen(uiState, onAction)` 형태인지 확인한다.
- Screen 내부에서 ViewModel, Repository, 서버, DB를 직접 호출하지 않게 한다.
- 실제 연결이 필요한 코드는 Route에 남긴다.

### 2. Fake 상태 준비

- 실제로 발생 가능한 대표 상태만 만든다.
- 최소 Content 상태를 제공한다.
- 필요한 경우 Loading, Empty, Error, Long Content, Disabled 상태를 추가한다.
- Fake 데이터에는 개인정보, API Key, 실제 사용자 기록을 넣지 않는다.
- 실제 사용자 DB를 Debug Catalog에서 직접 읽지 않는다. (`30-production-engineering.mdc`)

### 3. Catalog 등록

- 화면 ID는 `agent/FIGMA_SCREEN_MAP.md`의 Screen ID와 맞춘다.
- 화면명, 카테고리, 상태명, 설명을 등록한다.
- 목록에서 항목을 눌렀을 때 해당 Fake UiState의 Screen을 직접 렌더링한다.
- Route나 실제 앱 Navigation을 거치지 않는다.

### 4. Debug 전용 여부 확인

- Catalog Activity, Registry, Fake 상태는 `src/debug`에 둔다.
- Release 소스에서 Debug Catalog 클래스를 참조하지 않는다.
- Release 빌드에 Debug Catalog Activity와 Launcher가 포함되지 않는 구조를 유지한다.

### 5. 실행 확인

- Debug 빌드에서 Catalog를 실행한다.
- 카테고리 목록에서 화면을 찾을 수 있는지 확인한다.
- 등록한 모든 대표 상태가 크래시 없이 열리는지 확인한다.
- 뒤로 가기와 스크롤이 정상 작동하는지 확인한다.

### 6. 기록

- `agent/FIGMA_SCREEN_MAP.md`의 Debug Catalog 열을 `REGISTERED`로 갱신한다.
- 실행하지 못했다면 `NOT VERIFIED`와 이유를 기록한다.

## Completion

아래 조건을 모두 만족해야 등록 완료다.

- Debug Catalog 목록에 화면이 보임
- 실제 앱 링크 없이 직접 열림
- 최소 Content 상태가 실행됨
- 필요한 대표 상태가 Fake UiState로 실행됨
- Release 빌드에 포함되지 않는 위치에 구현됨
