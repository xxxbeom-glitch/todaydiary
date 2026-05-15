import {
  collection,
  doc,
  onSnapshot,
  runTransaction,
  serverTimestamp,
  setDoc,
  Timestamp,
  type Unsubscribe,
} from 'firebase/firestore';
import { getDiaryFirestore } from '../../lib/firebase';
import { DIARY_FIELDS } from '../../lib/firestore/fields';
import { FIRESTORE, resolveDiaryDocId } from '../../lib/firestore/paths';
import { getUpdatedAtMs, mapDiaryDocument, sortDiaryEntries } from './mapDocument';
import type { DiaryEntry } from './types';
import { formatWrittenAtText, parseDateParts } from './writtenAt';

function diariesCollection(uid: string) {
  return collection(getDiaryFirestore(), FIRESTORE.users, uid, FIRESTORE.diaries);
}

/** Android `FirestoreDiaryRepository.newEntryId()` */
export function newEntryId(): string {
  return crypto.randomUUID();
}

/**
 * Android `FirestoreDiaryRepository.listenEntries` — `users/{uid}/diaries` onSnapshot
 */
export function subscribeDiaryEntries(
  uid: string,
  onUpdate: (entries: DiaryEntry[]) => void,
  onError?: (error: Error) => void,
): Unsubscribe {
  return onSnapshot(
    diariesCollection(uid),
    (snapshot) => {
      const entries: DiaryEntry[] = [];
      const updatedAtById = new Map<string, number>();

      for (const snap of snapshot.docs) {
        const data = snap.data() as Record<string, unknown>;
        const entry = mapDiaryDocument(snap.id, data);
        if (!entry) continue;
        entries.push(entry);
        updatedAtById.set(entry.id, getUpdatedAtMs(data));
      }

      onUpdate(sortDiaryEntries(entries, updatedAtById));
    },
    (err) => onError?.(err),
  );
}

/** @deprecated `subscribeDiaryEntries` 사용 */
export const listenDiaryEntries = subscribeDiaryEntries;

/** Android `saveEntry` — merge set (공통 저장 로직) */
async function writeDiaryEntry(
  uid: string,
  entry: DiaryEntry,
  writtenAt?: Date,
): Promise<void> {
  const docId = resolveDiaryDocId(entry.id, entry.date);
  const now = writtenAt ?? new Date();
  const { year, month, day } = parseDateParts(entry.date);
  const zone = Intl.DateTimeFormat().resolvedOptions().timeZone;

  const payload: Record<string, unknown> = {
    [DIARY_FIELDS.userId]: uid,
    [DIARY_FIELDS.date]: entry.date,
    [DIARY_FIELDS.year]: year,
    [DIARY_FIELDS.month]: month,
    [DIARY_FIELDS.day]: day,
    [DIARY_FIELDS.writtenAt]: Timestamp.fromDate(now),
    [DIARY_FIELDS.writtenAtText]: formatWrittenAtText(now),
    [DIARY_FIELDS.writtenAtTimeZone]: zone,
    [DIARY_FIELDS.body]: entry.body,
    [DIARY_FIELDS.photos]: entry.photos,
    [DIARY_FIELDS.updatedAt]: serverTimestamp(),
    [DIARY_FIELDS.id]: entry.id.trim() !== '' ? entry.id : docId,
  };

  await setDoc(
    doc(getDiaryFirestore(), FIRESTORE.users, uid, FIRESTORE.diaries, docId),
    payload,
    { merge: true },
  );
}

/** 신규 일기 — `id`는 `newEntryId()`로 미리 생성 권장 */
export async function createDiaryEntry(
  uid: string,
  entry: DiaryEntry,
  writtenAt?: Date,
): Promise<void> {
  return writeDiaryEntry(uid, entry, writtenAt);
}

/** 기존 일기 수정 — Android `saveEntry`와 동일 (merge) */
export async function updateDiaryEntry(
  uid: string,
  entry: DiaryEntry,
  writtenAt?: Date,
): Promise<void> {
  return writeDiaryEntry(uid, entry, writtenAt);
}

/** Android `saveEntry` — create/update 공용 */
export const saveDiaryEntry = writeDiaryEntry;

/**
 * Android `FirestoreDiaryRepository.deleteEntry`
 * — `deletedDiaries` 복사 후 `diaries` 삭제 (트랜잭션)
 */
export async function deleteDiaryEntry(
  uid: string,
  id: string,
  date: string,
): Promise<void> {
  const docId = resolveDiaryDocId(id, date);
  const db = getDiaryFirestore();
  const dRef = doc(db, FIRESTORE.users, uid, FIRESTORE.diaries, docId);
  const delRef = doc(db, FIRESTORE.users, uid, FIRESTORE.deletedDiaries, docId);

  await runTransaction(db, async (transaction) => {
    const snap = await transaction.get(dRef);
    if (!snap.exists()) return;
    const data = { ...snap.data() } as Record<string, unknown>;
    data[DIARY_FIELDS.deletedAt] = serverTimestamp();
    if (data[DIARY_FIELDS.userId] == null || data[DIARY_FIELDS.userId] === '') {
      data[DIARY_FIELDS.userId] = uid;
    }
    transaction.set(delRef, data);
    transaction.delete(dRef);
  });
}
