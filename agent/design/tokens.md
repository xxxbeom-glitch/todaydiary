# Design Tokens — Scribe-like cool light (web)

웹 SoT: `web/src/index.css` `:root`와 동기화한다.  
방향: **쿨 라이트** — 바깥 쿨 그레이, 흰 에디터 면, 뉴트럴 잉크. 웜 크림·살구 액센트는 쓰지 않는다.  
레퍼런스: Scribe형 노트 UI 스크린샷 컬러 샘플.

## Color

| Token | Value | 용도 |
|-------|-------|------|
| `--color-canvas` | `#eceef0` | 브라우저 뒤 배경 |
| `--color-bg` | `#f4f5f7` | 셸·좌측 목록 |
| `--color-surface` | `#ffffff` | 우측 에디터·카드 |
| `--color-surface-muted` | `#e8eaed` | press/hover |
| `--color-text-primary` | `#1a1a1a` | 제목·본문 강조 |
| `--color-text-secondary` | `#5c5c60` | 보조 메타 |
| `--color-text-muted` | `#8e8e93` | caption·placeholder·비활성 |
| `--color-border` | `#e4e6e9` | 얇은 구분선 |
| `--color-accent` | `#2c2c2e` | FAB·포커스 (거의 블랙) |
| `--color-accent-soft` | `#eef0f3` | 선택·약한 강조 |
| `--color-danger` | `#c0392b` | 오류 |
| `--color-danger-soft` | `#f8ecea` | 오류 배경 |
| `--color-overlay` | `rgb(26 26 26 / 0.14)` | 모달 딤 |

## List item

- `.app-list-item`: 보더 없음, 선택 시 `--color-accent-soft`

## Typography

| Token / class | 값 |
|---------------|-----|
| `--font-sans` | system-ui stack (당분간) |
| `.type-page-title` | 22 / 700 |
| `.type-section-title` | 17 / 700 |
| `.type-body` | 15 / 400 · lh 1.65 |
| `.type-caption` | 12 / 400 · muted |

## Radius / Layout

| Token | Value |
|-------|-------|
| `--radius-card` | `22px` |
| `--radius-input` | `18px` |
| `--radius-pill` | `999px` |
| `--app-max-width` | `430px` (모바일) |
| `--app-max-width-desktop` | `1024px` 상한 (≥1024) |
| `--app-frame-height-desktop` | `768px` 상한 |
| `--app-frame-ratio` | `1024 / 768` — 뷰포트에 맞게 축소 |
| `--frame-inset` | `24px` |
| `--desktop-list-width` | `88px` — 날짜 타일 레일 |
| `--shadow-shell` | `var(--shadow-shell-x) var(--shadow-shell-y) 28px …` — 기본 `0 8px`, PC에서 커서 추적 |
| `--shadow-doc` | `0 4px 24px rgb(26 26 26 / 0.06)` — 안쪽 문서 프레임 |
| `--radius-doc` | `18px` |
| `--doc-inset` | `24px` — 셸↔문서 카드 간격 |
| `--shadow-card` | `0 1px 3px rgb(26 26 26 / 0.04)` |
| `--shadow-fab` | `0 4px 14px rgb(26 26 26 / 0.14)` |

## Do not

- 웜 크림·세피아·살구 클레이로 되돌리지 않는다
- 보라/인디고 그라데이션 테마를 쓰지 않는다
- hex를 컴포넌트에 하드코딩하지 않는다 (토큰만)
