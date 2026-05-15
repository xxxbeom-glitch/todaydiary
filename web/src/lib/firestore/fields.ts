/**
 * Firestore 필드명 — Android `FirestoreDiaryRepository.saveEntry` payload 기준.
 */

export const DIARY_FIELDS = {
  userId: 'userId',
  id: 'id',
  date: 'date',
  year: 'year',
  month: 'month',
  day: 'day',
  body: 'body',
  photos: 'photos',
  writtenAt: 'writtenAt',
  writtenAtText: 'writtenAtText',
  writtenAtTimeZone: 'writtenAtTimeZone',
  updatedAt: 'updatedAt',
  deletedAt: 'deletedAt',
} as const;

export const USER_FIELDS = {
  uid: 'uid',
  email: 'email',
  displayName: 'displayName',
  photoUrl: 'photoUrl',
  createdAt: 'createdAt',
  updatedAt: 'updatedAt',
  lastLoginAt: 'lastLoginAt',
} as const;
