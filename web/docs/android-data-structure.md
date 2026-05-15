# Android 앱 Firestore 데이터 구조 분석

> 분석 기준: `app/src/main/java/com/todaydiary/app/` (Android 코드는 변경하지 않음)  
> 웹앱은 이 문서와 동일한 경로·필드·규칙을 따라야 Android와 데이터가 호환됩니다.

**요약 스키마:** [`firestore-schema.md`](firestore-schema.md)  
**웹 구현:** `web/src/lib/firebase.ts`, `web/src/features/auth`, `web/src/features/diary`

## Firebase 프로젝트

| 항목 | 값 |
|------|-----|
| Project ID | `cole-c3f96` |
| Firestore **named database** | `diary` (default DB가 아님) |
| Storage bucket | `cole-c3f96.firebasestorage.app` |
| Android 패키지 | `com.todaydiary.app` |

웹 클라이언트는 반드시 **같은 Firebase 프로젝트**에 연결하고, Firestore 인스턴스를 **`diary` 데이터베이스**로 초기화해야 합니다.

```ts
// Web (Firebase JS v9+)
import { getFirestore } from 'firebase/firestore';
const db = getFirestore(app, 'diary');
```

Android (`FirestoreInstances.kt`):

```kotlin
const val DIARY_DATABASE_ID = "diary"
FirebaseFirestore.getInstance(FirebaseApp.getInstance(), DIARY_DATABASE_ID)
```

---

## 인증 (Auth)

- **Provider**: Firebase Auth + Google Sign-In
- **UID**: `FirebaseUser.uid` — 모든 Firestore 경로의 `{uid}`와 동일
- Android는 `FirebaseAuth.getInstance()` 기본 Auth 인스턴스 사용 (named DB와 무관)
- 로그인 직후 `FirestoreUserRepository.ensureUserDocument()` 호출

웹도 동일하게:

1. `signInWithPopup(GoogleAuthProvider)` (또는 redirect)
2. 로그인 성공 시 `users/{uid}` 프로필 문서 upsert

---

## 컬렉션 경로

### 사용자 프로필

```
users/{uid}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `uid` | string | Firebase Auth UID |
| `email` | string | |
| `displayName` | string | |
| `photoUrl` | string | |
| `createdAt` | timestamp | 최초 생성 시만 |
| `updatedAt` | serverTimestamp | |
| `lastLoginAt` | serverTimestamp | |

저장: `set(payload, { merge: true })`

### 일기 (활성)

```
users/{uid}/diaries/{docId}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `userId` | string | ○ | Auth UID |
| `id` | string | ○ | 논리 ID (보통 UUID, 레거시는 날짜 문자열) |
| `date` | string | ○ | `YYYY-MM-DD` (`LocalDate.toString()`) |
| `year` | number | ○ | `date.year` |
| `month` | number | ○ | `date.monthValue` (1–12) |
| `day` | number | ○ | `date.dayOfMonth` |
| `body` | string | ○ | 일기 본문 |
| `photos` | string[] | ○ | Storage URL 또는 에셋 경로 (1차 웹은 빈 배열 유지) |
| `writtenAt` | timestamp | ○ | 클라이언트가 지정한 작성 시각 |
| `writtenAtText` | string | ○ | `ISO_OFFSET_DATE_TIME` (기기 타임존) |
| `writtenAtTimeZone` | string | ○ | 예: `Asia/Seoul` |
| `updatedAt` | serverTimestamp | ○ | 저장 시 `FieldValue.serverTimestamp()` |

**문서 ID (`docId`) 규칙** (`FirestoreDiaryRepository.saveEntry`):

1. `entry.id`가 비어 있지 않으면 → **문서 ID = `entry.id`** (UUID)
2. 비어 있으면 (레거시) → **문서 ID = `entry.date.toString()`** (`YYYY-MM-DD`)

읽기 시 ID 복원:

1. 필드 `id`가 있으면 사용
2. 없으면 `doc.id` 사용

### 삭제된 일기 (소프트 삭제 아카이브)

```
users/{uid}/deletedDiaries/{docId}
```

삭제 시 **트랜잭션**으로:

1. `diaries/{docId}` 문서 전체를 읽음
2. `deletedAt: serverTimestamp()` 추가 (없으면 `userId` 보강)
3. `deletedDiaries/{docId}`에 `set`
4. `diaries/{docId}` `delete`

웹에서 삭제 API를 맞출 때 동일 트랜잭션을 사용해야 Android와 일관됩니다.

---

## 문서 ID 생성 (신규 일기)

```kotlin
fun newEntryId(): String = UUID.randomUUID().toString()
```

Android `MainActivity`: `+` 버튼 → `activeEntryId = repo.newEntryId()` 후 에디터 진입.

웹 신규 작성도 **저장 전에 UUID를 생성**하고, 그 값을 문서 ID·`id` 필드 모두에 사용합니다.

---

## CRUD 동작 요약

### 목록 / 실시간 구독

- 경로: `users/{uid}/diaries`
- **서버 `orderBy` 없음** — 전체 스냅샷 수신 후 클라이언트 정렬
- 정렬: `updatedAt` 내림차순 → `date` 내림차순 → `id` 내림차순
- `updatedAt` 없는 레거시 문서는 `0`으로 취급

월별 목록(UI): `YearMonth.from(entry.date) == currentMonth` 필터 후  
`date` 내림차순 → `id` 내림차순 (Android `MainActivity` listEntries)

### 저장

- `set(payload, { merge: true })` on `users/{uid}/diaries/{docId}`
- `writtenAt`: 에디터 자동 저장 시각 (`Instant`, 없으면 `now()`)
- `updatedAt`: 항상 서버 타임스탬프

### 삭제

- `deleteEntry(uid, id, date)` — `docId = id.ifBlank { date.toString() }`
- 위 트랜잭션으로 `deletedDiaries` 복사 후 `diaries`에서 제거

---

## 앱 모델 (`DiaryEntry.kt`)

```kotlin
data class DiaryEntry(
    val id: String = "",
    val date: LocalDate,
    val body: String,
    val photos: List<String> = emptyList(),
)
```

웹 TypeScript 대응:

```ts
export interface DiaryEntry {
  id: string;
  date: string; // YYYY-MM-DD
  body: string;
  photos?: string[]; // 1차 웹: 읽기만 하거나 항상 []
}
```

---

## Android 전용 (웹 1차 범위 밖)

### 로컬 DraftStore

- SharedPreferences `diary_drafts`
- 비로그인·오프라인 초안은 **기기에만** 존재
- 로그인 후 Firestore와 병합·마이그레이션 (`MainActivity` LaunchedEffect)
- **웹에는 DraftStore 없음** — 로그인 필수 또는 브라우저 로컬 스토리지는 별도 정책

### 본문 중복 제거 (`DiaryBodyDedupe`)

- 정규화: `\r\n`→`\n`, 연속 공백→한 칸, trim
- 같은 `date` + 정규화된 `body`면 1건만 표시 (사진 수·id로 타이브레이크)
- 웹 목록에서도 동일 규칙 적용 권장 (선택)

### 사진 (`DiaryPhotoStorage`)

- Storage: `diary-photo/{uid}/{entryId}/...`
- 1차 웹 범위 **제외** — 저장 시 `photos: []` 유지하거나 기존 배열 merge 시 덮어쓰지 않도록 주의

---

## 웹 구현 체크리스트

- [ ] Firebase Web App 등록 후 `.env`에 설정
- [ ] `getFirestore(app, 'diary')` 사용
- [ ] Google 로그인 → `users/{uid}` ensure
- [ ] `users/{uid}/diaries` listener + 동일 정렬
- [ ] 저장 시 Android와 동일 payload 필드
- [ ] 삭제 시 `deletedDiaries` 트랜잭션
- [ ] 신규 ID: `crypto.randomUUID()`
- [ ] 사진 UI/업로드 미구현 (필드만 호환)

---

## 참고 소스 파일

| 파일 | 역할 |
|------|------|
| `ui/models/DiaryEntry.kt` | 도메인 모델 |
| `data/FirestoreDiaryRepository.kt` | 일기 CRUD·리스너·삭제 트랜잭션 |
| `data/FirestoreInstances.kt` | named DB `diary` |
| `data/FirestoreUserRepository.kt` | 사용자 문서 |
| `auth/FirebaseAuthState.kt` | Auth 상태 구독 |
| `data/DraftStore.kt` | 로컬 초안 (Android only) |
| `MainActivity.kt` | UI 병합·월 필터·저장 흐름 |
