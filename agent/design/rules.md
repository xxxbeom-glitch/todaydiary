# Design Rules

디자인 시안만으로 알 수 없는 **앱 전반 규칙**. SoT 토큰: `agent/design/tokens.md` · `web/src/index.css`.

## Product / Interaction

- 1차 범위: 텍스트 일기 (웹 사진 업로드 UI 없음) — `agent/PROJECT_SPEC.md`
- 테마: cool light + dark. 웜 크림·살구·보라 그라데이션 금지
- 설정(테마·폰트·글자 크기)은 `localStorage`에 즉시 저장 (`web/src/lib/preferences.ts`). 계정 동기화 없음
- 컴포넌트 hex 하드코딩 금지 (토큰만)

## Typography split

- **UI**(헤더·목록·설정·캘린더 크롬·버튼): 항상 Pretendard (`--font-sans`)
- **작성·뷰**(날짜 라벨·본문 textarea/read-body): 설정 폰트 (`--font-prose`)
- 글자 크기 설정은 `--font-size-prose`만 변경. UI `--font-size-base`는 고정
- 본문 폰트 weight는 파일에 존재하는 face와 맞출 것 (`font-synthesis: none` + 없는 weight → Pretendard 폴백 주의)

## Layout (Web)

- PC(≥1024): 가운데 프레임 · 좌 날짜 타일 레일 · 우 작성/뷰 · 상단 월 네비+설정
- 모바일: **풀스크린**. 상단 가로 스크롤 날짜 레일 + 하단 뷰/작성 동시 배치. 새 글 FAB(이번 달만)
- PC: 바깥 셸·림·캔버스. 모바일에는 프레임/림 적용하지 않음
- 삭제는 작성/뷰에서 **우측 하단 고스트 아이콘** (카드형 FAB·큰 inset 금지)
- 헤더 **월 선택**: 년·월만 (`MonthPickerModal`). 일 그리드 없음
- 작성 **작성일 피커**: 일 선택 유지. 선택 UI는 연한 원형 fill, 라이브러리 기본 파란 액센트 금지
- 뷰: 본문 더블클릭 → 수정. 에디터에서 본문 밖 클릭 → 저장(기존 글이면 뷰 복귀)

## Accessibility

- 주요 터치 영역 최소 44px 권장
- 색만으로 상태(오류/선택/삭제)를 전달하지 않는다 (아이콘·라벨 병행)

## Platform (Web)

- PC 셸: 뷰포트 스크롤 잠금, 프레임만 스크롤
- `react-day-picker` 커스텀은 **unlayered** CSS로 둘 것 (`@layer` 안이면 기본 blue가 이김)

## Platform (Android)

- System bars / IME는 WindowInsets
- Figma iOS 상태바 padding은 Android inset으로 대체

## Debug Catalog

- Android 주요 Screen은 Debug Catalog Fake UiState 등록
- `agent/specs/DEBUG_SCREEN_CATALOG_GUIDE.md`

## Do not

- MCP hex/절대좌표 그대로 복제
- 모션을 요청 없이 필수 구현으로 확대
- 다른 제품(Liftly 등) 디자인 규칙을 그대로 가져오기
- 그리운한글 등 제거된 폰트 옵션 재추가 (요청 없이)
