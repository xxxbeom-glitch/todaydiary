/**
 * Firestore 경로 — Android `FirestoreDiaryRepository` / `FirestoreUserRepository` 와 동일.
 */

export const FIRESTORE = {
  users: 'users',
  diaries: 'diaries',
  deletedDiaries: 'deletedDiaries',
} as const;

export function userDocPath(uid: string): string {
  return `${FIRESTORE.users}/${uid}`;
}

export function userDiaryDocPath(uid: string, docId: string): string {
  return `${userDocPath(uid)}/${FIRESTORE.diaries}/${docId}`;
}

export function userDeletedDiaryDocPath(uid: string, docId: string): string {
  return `${userDocPath(uid)}/${FIRESTORE.deletedDiaries}/${docId}`;
}

/** Android `saveEntry` / `deleteEntry` 문서 ID */
export function resolveDiaryDocId(entryId: string, date: string): string {
  return entryId.trim() !== '' ? entryId : date;
}
