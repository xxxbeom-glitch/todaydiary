/**
 * Android `com.todaydiary.app.ui.models.DiaryEntry`
 * @see app/src/main/java/com/todaydiary/app/ui/models/DiaryEntry.kt
 *
 * Firestore `users/{uid}/diaries/{docId}` 필드와 1:1 대응 (date는 YYYY-MM-DD 문자열).
 */
export interface DiaryEntry {
  /** 논리 ID (보통 UUID). 레거시는 날짜 문자열 */
  id: string;
  /** `LocalDate.toString()` → `YYYY-MM-DD` */
  date: string;
  body: string;
  /** Android `photos` 필드 — 1차 웹은 빈 배열 또는 기존 값 유지 */
  photos: string[];
}

/** Android `FirestoreDiaryRepository.saveEntry` payload */
export interface DiarySavePayload {
  userId: string;
  id: string;
  date: string;
  year: number;
  month: number;
  day: number;
  body: string;
  photos: string[];
  writtenAt: unknown;
  writtenAtText: string;
  writtenAtTimeZone: string;
  updatedAt: unknown;
}
