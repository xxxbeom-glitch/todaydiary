# Design Source

Cursor가 화면을 구현할 때 Figma MCP와 **함께** 참고하는 디자인 소스다.

## 구성

| 경로 | 내용 |
|------|------|
| `tokens.md` | 공통 색·타이포·간격·radius 등 기본 토큰 |
| `rules.md` | 디자인만으로 알 수 없는 제품·UI·접근성 규칙 |
| `screenshots/` | Figma 원본 화면 스크린샷 (참조용) |
| `handoff/` | 플러그인 export (FLOWS·토큰 힌트). 의도·흐름은 Screen Spec 우선 |

## 읽는 순서 (구현 시)

1. `agent/TASK_CONTRACT.md`
2. `agent/LIFTLY_SCREEN_SPEC.md` (해당 화면·흐름)
3. `agent/design/tokens.md`
4. `agent/design/rules.md`
5. 해당 화면 스크린샷 (`screenshots/`)
6. Figma MCP (`get_design_context`) 또는 Frame 링크
7. (보조) `agent/design/handoff/` — FLOWS·컴포넌트 힌트

## 충돌 시 우선순위

1. 사용자 최신 요청 / `TASK_CONTRACT`
2. `agent/LIFTLY_SCREEN_SPEC.md` / `agent/PROJECT_SPEC.md`
3. `agent/design/rules.md`
4. `agent/design/tokens.md`
5. Figma Variables / Annotation
6. Figma MCP가 준 hex·절대 좌표 (최하위 — 토큰으로 치환)
7. `handoff/SCREENS.md` 컴포넌트 합산 목록 (신뢰도 낮음 — 무시 가능)

스크린샷은 시각 의도 확인용이며, 토큰·규칙을 덮어쓰지 않는다.
Status bar(iPhone 목업)는 구현 대상이 아니다.
