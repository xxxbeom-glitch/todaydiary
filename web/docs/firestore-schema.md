# Firestore 스키마 (Android 기준)

> 출처: `DiaryEntry.kt`, `FirestoreDiaryRepository.kt`, `FirestoreUserRepository.kt`, `FirestoreInstances.kt`

## 데이터베이스

| 항목 | 값 |
|------|-----|
| Named DB | `diary` (`FirestoreInstances.DIARY_DATABASE_ID`) |
| Project ID | `cole-c3f96` |

웹: `getFirestore(app, 'diary')` — [`src/lib/firebase.ts`](../src/lib/firebase.ts)

---

## 경로

| 용도 | 경로 |
|------|------|
| 사용자 프로필 | `users/{uid}` |
| 활성 일기 | `users/{uid}/diaries/{docId}` |
| 삭제 아카이브 | `users/{uid}/deletedDiaries/{docId}` |

`{uid}` = Firebase Auth `user.uid`  
`{docId}` = `entry.id`가 있으면 그 값, 없으면 `entry.date` (`YYYY-MM-DD`, 레거시)

---

## DiaryEntry (앱 모델)

| 필드 | Kotlin 타입 | Firestore |
|------|-------------|-----------|
| `id` | `String` | 문서 필드 `id` + 문서 ID와 동일한 경우 많음 |
| `date` | `LocalDate` | `date` 문자열 `YYYY-MM-DD` |
| `body` | `String` | `body` |
| `photos` | `List<String>` | `photos` (1차 웹: 읽기·유지만, 업로드 없음) |

읽기 시 ID: `id` 필드 → 없으면 `doc.id`

---

## 일기 문서 필드 (`saveEntry` payload)

| 필드 | 타입 | 비고 |
|------|------|------|
| `userId` | string | Auth UID |
| `id` | string | `entry.id` 또는 `docId` |
| `date` | string | `YYYY-MM-DD` |
| `year` | number | |
| `month` | number | 1–12 (`monthValue`) |
| `day` | number | |
| `body` | string | |
| `photos` | string[] | |
| `writtenAt` | timestamp | 클라이언트 시각 |
| `writtenAtText` | string | `ISO_OFFSET_DATE_TIME` |
| `writtenAtTimeZone` | string | 예: `Asia/Seoul` |
| `updatedAt` | serverTimestamp | 저장 시마다 갱신 |

저장: `set(payload, { merge: true })`

---

## listenEntries (읽기·정렬)

- 쿼리 `orderBy` 없음 — 컬렉션 전체 스냅샷
- 클라이언트 정렬: `updatedAt` ↓ → `date` ↓ → `id` ↓
- `updatedAt` 없으면 `0`

---

## deleteEntry (삭제)

1. `diaries/{docId}` 읽기
2. 없으면 종료 (성공)
3. `deletedAt` = serverTimestamp, `userId` 없으면 보강
4. `deletedDiaries/{docId}`에 set
5. `diaries/{docId}` delete  
→ **트랜잭션**

---

## newEntryId

`UUID.randomUUID()` — 신규 일기는 저장 전 ID 생성

---

## 사용자 문서 (`ensureUserDocument`)

| 필드 | 타입 |
|------|------|
| `uid` | string |
| `email` | string |
| `displayName` | string |
| `photoUrl` | string |
| `updatedAt` | serverTimestamp |
| `lastLoginAt` | serverTimestamp |
| `createdAt` | serverTimestamp | 최초 생성 시만 |

`set(merge: true)`

---

## 웹 코드 매핑

| Android | Web |
|---------|-----|
| `FirestoreInstances.diary` | `lib/firebase.ts` → `getDiaryFirestore()` |
| `FirestoreUserRepository` | `features/auth/userRepository.ts` |
| `FirestoreDiaryRepository` | `features/diary/diaryRepository.ts` |
| `DiaryEntry` | `features/diary/types.ts` |
