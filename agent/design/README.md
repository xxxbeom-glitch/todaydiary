# Design Source

Cursor가 화면을 구현할 때 참고하는 디자인 소스다.

## 구성

| 경로 | 내용 |
|------|------|
| `tokens.md` | 색·타이포·간격·radius (light/dark) |
| `rules.md` | 시안만으로 알 수 없는 제품·UI 규칙 |
| `screenshots/` | 참조 스크린샷 (있으면) |

## 읽는 순서 (웹 UI 구현 시)

1. `agent/TASK_CONTRACT.md` (활성 작업이 있으면)
2. `agent/PROJECT_SPEC.md`
3. `agent/design/tokens.md`
4. `agent/design/rules.md`
5. `.cursor/rules/60-web-ui.mdc` (웹 파일 작업 시)
6. 해당 화면 스크린샷 / Figma (있으면)

## 충돌 시 우선순위

1. 사용자 최신 요청 / `TASK_CONTRACT`
2. `agent/PROJECT_SPEC.md`
3. `agent/design/rules.md`
4. `agent/design/tokens.md`
5. Figma / 스크린샷 (최하위 — 토큰으로 치환)

스크린샷은 시각 의도 확인용이며, 토큰·규칙을 덮어쓰지 않는다.
