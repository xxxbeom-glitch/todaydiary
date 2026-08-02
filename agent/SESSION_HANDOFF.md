# Session Handoff

다음 세션에서 바로 이어가기 위해 현재 상태만 짧게 기록한다. 과거 이력 전체를 복사하지 않는다.

## Current Task

- Task ID: LIBRARY-IMPORT-COMPOSE
- Status: COMPLETED

## Completed

- `markdown` 라이브러리 `compose-android` 팩 이식
- `agent/PROJECT_SPEC.md` · `00-project-core.mdc`에 앱/웹 경계·Firestore SoT 반영
- Liftly 잔여 문구를 하루기록 기준으로 정리 (`design/rules`, production 규칙, Debug Catalog 가이드)

## Last Successful Verification

- 문서·규칙 이식만 (앱/웹 빌드 미실행) — `NOT VERIFIED`

## Open Blockers

- 없음

## Files In Progress

- 없음

## Next Action

- 리디자인 시: `agent/design/tokens.md` 작성, `FIGMA_SCREEN_MAP` 채우기
- 웹 작업이 커지면 `markdown`의 `web` 팩에서 필요한 `.mdc`만 선별 추가
- (선택) Issue Bridge가 필요하면 `-IssueBridge`로 추가

## Resume Command

```text
agent/PROJECT_SPEC.md 기준으로 이어서. Android SoT, web은 클라이언트. 리디자인 전까지 형태 유지.
```
