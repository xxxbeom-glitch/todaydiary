# PROJECT_SPEC — 하루기록 (Today Diary)

## 제품

- 이름: 하루기록 (Today Diary)
- 한 줄 설명: 날짜 단위 일기 — Android 네이티브 + 동일 Firestore를 쓰는 웹 클라이언트
- 플랫폼: android + web
- 스택:
  - `app/`: Kotlin, Jetpack Compose, Firebase (Auth, Firestore named DB `diary`, Storage)
  - `web/`: React 19, Vite, Tailwind 4, Firebase JS
  - 배포: 웹은 Vercel (`vercel.json` → `web/dist`)

## 범위

### 한다
- Android·웹에서 텍스트 일기 CRUD (목록 / 작성·수정 / 상세)
- Firebase Auth 기준 `users/{uid}/diaries` 동기화
- Firestore 스키마·필드 의미를 Android 구현과 `web/docs/`에 맞춤
- (추후) 형태 유지 리디자인 — UI/토큰/Figma 갱신

### 안 한다 (1차)
- 웹에서 사진 업로드 UI (읽기·기존 `photos` 유지만)
- Cafe24 / WordPress / 무관한 플랫폼 지침
- 스키마를 웹 편의로 일방 변경

## 모노레포 경계 (필수)

| 작업 | 수정 허용 | 금지 |
|------|-----------|------|
| Android | `app/` (+ 필요 시 `fonts/`, `restore/`, 루트 Gradle) | `web/` 임의 수정 |
| Web | `web/` (+ 루트 `vercel.json` 등 배포만) | `app/` 임의 수정 |
| Firestore 스키마 | Android 코드 + `web/docs/firestore-schema.md` 등 문서 동기화 | 웹만 보고 필드·경로 새로 발명 |

Named DB: `diary` (`FirestoreInstances.DIARY_DATABASE_ID` / `getFirestore(app, 'diary')`).

## 위험 구역 (손대기 전 확인)

| 구역 | 왜 위험한지 | 건드릴 때 |
|------|-------------|-----------|
| `app/.../data/*Repository*`, `FirestoreInstances` | 스키마·경로 SoT | 웹 `lib/firestore`, docs 동시 갱신 |
| `firestore.rules` | 보안·접근 제어 | 규칙·클라이언트 가정 함께 검증 |
| `google-services.json`, `.env.local` | 비밀·프로젝트 식별 | 커밋·로그·handoff 금지 (`SECURITY.md`) |
| `restore/`, Pencake import | 대량 원문·마이그레이션 | 요청 있을 때만 |
| 일기 `body` / 사진 URL | 개인 일기 본문 | 로그·이슈 ZIP에 원문 넣지 않음 |

## Source of Truth

| 종류 | 경로 |
|------|------|
| 제품·경계 | `agent/PROJECT_SPEC.md` (이 문서) |
| Firestore 스키마 | Android 구현 + `web/docs/firestore-schema.md`, `web/docs/android-data-structure.md` |
| 디자인 토큰·규칙 | `agent/design/` (`tokens.md`는 리디자인 시 작성) |
| Figma 화면 맵 | `agent/FIGMA_SCREEN_MAP.md` (리디자인 시 채움) |
| Cursor 규칙 | `.cursor/rules/` |
| 현재 작업 계약 | `agent/TASK_CONTRACT.md` |

## 작업 전 질문 (에이전트)

1. 이번 변경의 Done When은?
2. 대상이 `app/`인가 `web/`인가? (둘 다면 경계를 명시)
3. Firestore 필드·경로를 바꾸는가? (바꾸면 Android SoT + 문서)
4. 검증 명령은? (예: Android 빌드 / `cd web && npm run build`)
5. 디자인 SoT는 Figma / `agent/design/` / 기존 코드 중 어디가 우선인가?
