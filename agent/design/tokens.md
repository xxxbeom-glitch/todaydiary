# Design Tokens — Clean Day · cozy (web)

웹 SoT: `web/src/index.css` `:root`와 동기화한다.  
방향: **클린 + 포근** — 밝은 따뜻한 오프화이트, 부드러운 웜 그레이 텍스트, 살구빛 액센트. 무거운 세피아·밤 톤은 쓰지 않는다.

## Color

| Token | Value | 용도 |
|-------|-------|------|
| `--color-canvas` | `#efe6dc` | 브라우저 뒤 배경 (컨테이너 밖) |
| `--color-bg` | `#faf7f4` | 콘텐츠 컨테이너·목록 패널 |
| `--color-surface` | `#ffffff` | 카드·입력·리스트 행 |
| `--color-surface-muted` | `#f3ebe4` | press/hover 배경 |
| `--color-text-primary` | `#2a2623` | 본문·제목 |
| `--color-text-secondary` | `#6e6560` | 보조 메타 |
| `--color-text-muted` | `#9a9088` | caption·placeholder |
| `--color-border` | `#ebe4dc` | 입력·헤더 등 얇은 선 |
| `--color-accent` | `#c68b6d` | FAB·포커스·강조 (살구 클레이) |
| `--color-accent-soft` | `#f7ebe4` | 약한 강조 배경 |
| `--color-danger` | `#c0392b` | 삭제·오류 |
| `--color-danger-soft` | `#f8ecea` | 오류 배경 |
| `--color-overlay` | `rgb(42 38 35 / 0.16)` | 모달 딤 |

## List item

- `.app-list-item`: **보더 없음**, `surface` + `--shadow-card`만으로 구분

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
| `--app-max-width-desktop` | `1080px` (≥1024) |
| `--desktop-list-width` | `340px` |
| `--shadow-shell` | PC 컨테이너 띄움 |
| `--page-pad-x` | `18px` |
| `--page-pad-top` | `20px` |
| `--page-pad-bottom` | `96px` |
| `--shadow-card` | `0 1px 3px rgb(42 38 35 / 0.05)` |
| `--shadow-fab` | `0 4px 14px rgb(198 139 109 / 0.28)` |

## Do not

- 무거운 크림·세피아·밤색 브라운으로 되돌리지 않는다
- 보라/인디고 그라데이션 테마를 쓰지 않는다
- 목록 항목에 보더를 다시 넣지 않는다
- hex를 컴포넌트에 하드코딩하지 않는다 (토큰만)
