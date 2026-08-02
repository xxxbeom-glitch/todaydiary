---
name: implement-figma-screen
description: >-
  Implements one Figma screen as Android Jetpack Compose UI with UiState,
  Preview, and Debug Catalog registration. Use when building or aligning a
  screen from Figma, or when adding screen states and catalog entries together.
---

# Implement Figma Screen

Figma 화면 하나를 Android Jetpack Compose로 구현할 때 사용하는 작업 절차다.

## Use when

- 새 화면을 구현할 때
- 기존 화면을 Figma 기준으로 다시 맞출 때
- 화면 상태, Preview, Debug Catalog 항목을 함께 추가할 때

## Required inputs

- 현재 작업이 작성된 `agent/TASK_CONTRACT.md`
- `agent/design/tokens.md`, `agent/design/rules.md`
- 해당 화면 스크린샷(`agent/design/screenshots/`, 있으면)
- Figma 프레임(MCP 또는 링크) 또는 명확한 화면 캡처
- 프로젝트의 기존 Theme, Token, Component 위치

## Procedure

### 1. 작업 계약 확인

- Goal, Required, Allowed Scope, Forbidden, Done When을 확인한다.
- Figma 참조가 불명확하면 임의 화면을 만들지 않는다.
- absolute/무명 레이어 위주 시안이면 정리 필요를 보고한다.

### 2. 기존 구조 탐색

- `agent/design/tokens.md`와 `agent/design/rules.md`를 읽는다.
- `agent/design/screenshots/`에서 해당 ScreenId 이미지가 있으면 확인한다.
- 같은 기능의 Screen, Route, UiState, 공통 컴포넌트가 있는지 검색한다.
- 디자인 토큰과 Theme 구현 위치를 확인한다.
- 재사용 가능한 요소와 새로 만들어야 할 요소를 구분한다.

### 3. 화면 상태 정의

- 실제로 필요한 Loading, Content, Empty, Error 등의 상태를 정한다.
- UI에 필요한 데이터만 포함하는 UiState를 사용한다.
- 사용자 입력은 명확한 Action 또는 callback으로 전달한다.

### 4. UI 구현

- 먼저 큰 정보 계층과 레이아웃을 구현한다.
- 다음으로 Typography, Color, Shape, Spacing을 맞춘다.
- 마지막으로 이미지, 아이콘, 세부 상태와 상호작용을 적용한다.
- Figma의 고정 좌표 대신 Android 적응형 레이아웃을 사용한다.
- 실제 데이터 연결은 Route에서, 화면 렌더링은 순수 Screen에서 담당한다.

### 5. 확인 경로 추가

- 대표 상태를 확인할 Compose Preview를 작성한다.
- `.cursor/skills/register-debug-screen/SKILL.md`에 따라 Debug Catalog에 화면과 대표 상태를 등록한다.
- Debug Catalog가 아직 없다면 `agent/specs/DEBUG_SCREEN_CATALOG_GUIDE.md`의 최소 구조로 한 번 생성한다.
- 실제 Navigation, 서버, DB 연결 전에도 Fake UiState로 화면이 열려야 한다.

### 6. 검증

- `scripts/verify-ui.ps1` 또는 프로젝트의 공식 검증 명령을 실행한다.
- Preview와 Debug Catalog에서 대표 상태가 정상 렌더링되는지 확인한다.
- 가능한 화면 크기와 텍스트 확대 상태를 확인한다.
- Figma와 의도하지 않은 차이를 정리한다.
- 단순 UI 작업에는 Production 검토 전체를 강제하지 않는다.
- 저장, 네트워크, 권한, 개인정보가 추가되면 `.cursor/skills/review-production-readiness/SKILL.md`를 실행한다.

### 7. 종료

- `agent/FIGMA_SCREEN_MAP.md`의 화면 상태와 Debug Catalog 등록 여부를 갱신한다.
- 변경 파일과 검증 결과를 보고한다.
- 미해결 문제는 `agent/SESSION_HANDOFF.md`에 기록한다.
- 반복 가능성이 있는 오류만 `agent/ERROR_LEDGER.md`에 추가한다.

## Output format

```text
Status: PASS | RETRY | BLOCKED | STOP
Implemented:
Debug Catalog: REGISTERED | NOT APPLICABLE | BLOCKED
Changed files:
Verification:
Figma differences:
Remaining issues:
```
