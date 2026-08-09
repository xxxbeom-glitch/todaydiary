# Design Rules

디자인 시안·스크린샷만으로 알 수 없는 규칙을 적는다.
화면 하나의 일시적 제약은 Figma Annotation으로 두고, **앱 전반에 반복되는 규칙**만 여기에 유지한다.

리디자인 전까지는 기존 `app/`·`web/` UI 패턴을 따르고, 확정된 규칙만 아래에 채운다.

## Product / Interaction

- 1차 범위: 텍스트 일기 (웹 사진 업로드 UI 없음) — `agent/PROJECT_SPEC.md`
- 웹 테마: **Clean Day** — `agent/design/tokens.md` · `web/src/index.css` `:root`
- 크림/세피아/테라코타·보라 그라데이션으로 되돌리지 않는다
- 컴포넌트에 hex 하드코딩 금지 (토큰만)

## Navigation / Scroll

- TBD

## Forms / Validation

- TBD

## Accessibility

- 주요 터치 영역 최소 48dp. Figma IconButton과 별개다
- 색만으로 상태(오류/선택)를 전달하지 않는다
- TBD

## Platform (Android)

- System bars / IME(Insets) 대응 필요 시 WindowInsets를 사용한다
- Figma의 iOS 상태바 padding은 Android에서 시스템 inset으로 대체한다
- TBD

## Platform (Web)

- TBD (리디자인 시 viewport·safe-area 등)

## Content / Data limits

- TBD (본문 길이, 목록 상한 등)

## Debug Catalog

- 주요 사용자 Screen은 Debug Catalog에 Fake UiState로 등록한다 (Android)
- 상세: `agent/specs/DEBUG_SCREEN_CATALOG_GUIDE.md`

## Do not

- 이 문서를 무시하고 MCP hex/절대좌표를 그대로 복제하지 않는다
- 모션을 필수 구현으로 확대 해석하지 않는다 (별도 요청 있을 때만)
- Liftly 등 다른 제품의 디자인 규칙을 그대로 가져오지 않는다
