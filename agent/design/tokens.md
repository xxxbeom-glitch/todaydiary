# Design Tokens — Clean Day (web)

웹 SoT: `web/src/index.css` `:root`와 동기화한다.  
방향: **클린 데이** — 거의 흰 배경, 중립 그레이 텍스트, 얇은 보더, 액센트 최소.

## Color

| Token | Value | 용도 |
|-------|-------|------|
| `--color-bg` | `#f6f6f5` | 앱 셸·페이지 배경 |
| `--color-surface` | `#ffffff` | 카드·입력·리스트 행 |
| `--color-surface-muted` | `#efefed` | press/hover 배경 |
| `--color-text-primary` | `#171717` | 본문·제목 |
| `--color-text-secondary` | `#5c5c5c` | 보조 메타 |
| `--color-text-muted` | `#8a8a8a` | caption·placeholder |
| `--color-border` | `#e4e4e2` | 얇은 구분선 |
| `--color-accent` | `#3a4a52` | FAB·포커스·강조 (슬레이트 잉크) |
| `--color-accent-soft` | `#eef1f2` | 약한 강조 배경 |
| `--color-danger` | `#c0392b` | 삭제·오류 |
| `--color-danger-soft` | `#f8ecea` | 오류 배경 |
| `--color-overlay` | `rgb(23 23 23 / 0.18)` | 모달 딤 |

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
| `--app-max-width` | `430px` |
| `--page-pad-x` | `18px` |
| `--page-pad-top` | `20px` |
| `--page-pad-bottom` | `96px` |
| `--shadow-card` | `0 1px 2px rgb(23 23 23 / 0.04)` |
| `--shadow-fab` | `0 4px 14px rgb(58 74 82 / 0.2)` |

## Do not

- 크림·세피아·테라코타 팔레트로 되돌리지 않는다
- 보라/인디고 그라데이션 테마를 쓰지 않는다
- hex를 컴포넌트에 하드코딩하지 않는다 (토큰만)
