# Figma Screen Map

Figma 화면과 Android 구현 위치를 연결하는 주소록이다. 화면을 구현하거나 이름을 바꿀 때 갱신한다.

리디자인 Figma가 정해지면 file key와 행을 채운다. 웹 화면은 Notes에 `web/` 경로를 적어도 된다.

| Screen ID | Figma Page / Frame | Figma Node | Android Route | Screen File | Main States | Debug Catalog | Status |
|---|---|---|---|---|---|---|---|
| TBD | TBD | TBD | TBD | TBD | Content | NOT_REGISTERED | NOT_STARTED |

## Status values

- `NOT_STARTED`
- `READY_FOR_IMPL` — Figma 시안 정리 완료, Cursor 구현 대기
- `IN_PROGRESS`
- `IMPLEMENTED`
- `VERIFIED`
- `BLOCKED`

## Debug Catalog values

- `NOT_REGISTERED`
- `REGISTERED`
- `NOT_APPLICABLE`
- `BLOCKED`

## Rules

- Screen ID는 프로젝트 전체에서 중복되지 않게 유지한다.
- Figma 이름만 쓰지 말고 가능한 경우 Node ID 또는 직접 식별 가능한 참조를 함께 기록한다.
- Android 파일이 이동하거나 이름이 바뀌면 즉시 갱신한다.
- 주요 사용자 화면은 Debug Catalog 등록 상태를 함께 관리한다.
- Figma와 코드의 의도적인 차이가 있다면 Notes 열을 추가해 이유를 기록한다.
