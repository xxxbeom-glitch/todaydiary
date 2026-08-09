# Design Tokens — Scribe-like cool light (web)

웹 SoT: `web/src/index.css` `:root` / `:root[data-theme='dark']`와 동기화한다.  
방향: **쿨 라이트** — 바깥 쿨 그레이, 흰 에디터 면, 뉴트럴 잉크. 웜 크림·살구 액센트는 쓰지 않는다.

## Color — Light

| Token | Value | 용도 |
|-------|-------|------|
| `--color-canvas` | `#eceef0` | 브라우저 뒤 배경 |
| `--color-bg` | `#f4f5f7` | 셸·좌측 목록·헤더 |
| `--color-surface` | `#ffffff` | 우측 작성·뷰 면 |
| `--color-surface-muted` | `#e8eaed` | press/hover |
| `--color-text-primary` | `#1a1a1a` | UI 텍스트 |
| `--color-text-prose` | `#2e2e32` | 작성·뷰 본문 (살짝 연한 잉크) |
| `--color-text-secondary` | `#5c5c60` | 보조 메타 |
| `--color-text-muted` | `#8e8e93` | caption·placeholder |
| `--color-border` | `#e4e6e9` | 얇은 구분선 |
| `--color-accent` | `#2c2c2e` | 포커스·강조 (거의 블랙) |
| `--color-accent-soft` | `#eef0f3` | 선택·약한 강조 |
| `--color-danger` | `#c0392b` | 삭제·오류 |
| `--color-danger-soft` | `#f8ecea` | 오류/삭제 hover 배경 |
| `--color-overlay` | `rgb(26 26 26 / 0.14)` | 모달 딤 |

## Color — Dark (`data-theme="dark"`)

| Token | Value | 용도 |
|-------|-------|------|
| `--color-canvas` | `#2a2a2e` | 바깥 배경 |
| `--shell-outer-bg` | `rgb(255 255 255 / 0.06)` | 셸 겉 림 |
| `--shell-outer-border` | `#fff 8%` | 겉 테두리 (희미) |
| `--shell-inner-border` | `#fff 6%` | 안쪽 셸 테두리 (희미) |
| `--color-bg` | `#1c1c1e` | 셸·목록·헤더 |
| `--color-surface` | `#2c2c2e` | 작성·뷰 면 |
| `--color-surface-muted` | `#3a3a3c` | press/hover |
| `--color-text-primary` | `#f5f5f7` | UI 텍스트 |
| `--color-text-prose` | `#e8e8ec` | 작성·뷰 본문 |
| `--color-text-secondary` | `#a1a1a6` | 보조 |
| `--color-text-muted` | `#8e8e93` | caption |
| `--color-border` | `#3a3a3c` | 구분선 |
| `--color-accent` | `#f5f5f7` | 강조 |
| `--color-accent-soft` | `#3a3a3c` | 약한 강조 |
| `--color-danger` | `#ff6b5c` | 삭제·오류 |
| `--color-danger-soft` | `#3b2422` | 삭제 hover |
| `--color-overlay` | `rgb(0 0 0 / 0.45)` | 모달 딤 |

## Typography

Weight는 Regular / Medium / Semibold만. Bold(700+) 금지.

| Token | 값 | 용도 |
|-------|-----|------|
| `--font-sans` | Pretendard (+ system) | UI 크롬 고정 |
| `--font-prose` | 설정 선택 폰트 | 작성·뷰(날짜·본문)만 |
| `--font-weight-regular` | `400` | |
| `--font-weight-medium` | `500` | |
| `--font-weight-semibold` | `600` | |
| `--font-size-base` | `15px` | UI (글자 크기 설정과 무관) |
| `--font-size-prose` | `14` / `15` / `18` | 작성·뷰 (설정: 작게·중간·크게) |

본문 선택 폰트: Pretendard · 교보손글씨 · 부크크고딕 · 부크크명조  
파일: `web/public/fonts/` · CDN Pretendard는 `web/index.html`

작성·뷰 본문: `line-height: 2`, `letter-spacing: ~0.025em`, ClearType(`font-smoothing: auto`)

## Layout

| Token | Value |
|-------|-------|
| `--app-max-width` | `430px` (모바일) |
| `--app-max-width-desktop` | `1024px` |
| `--app-frame-height-desktop` | `768px` |
| `--shell-outer-pad` | `12px` |
| `--desktop-list-width` | `104px` |
| `--page-pad-x` | 모바일 `28px` / PC `88px` |
| `--page-pad-top` | PC `36px` |
| `--page-pad-bottom` | 모바일 `96px` / PC `48px` |
| `--radius-shell` | `20px` |
| `--radius-card` | `22px` |
| `--radius-pill` | `999px` |

## Do not

- 웜 크림·세피아·살구·보라/인디고 그라데이션
- `react-day-picker` 기본 파란 액센트 방치 (unlayered 오버라이드 필수)
- 컴포넌트 hex 하드코딩 (토큰만)
- UI 크롬에 `--font-prose` 적용
