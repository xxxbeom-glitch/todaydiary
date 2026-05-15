import { Timestamp } from 'firebase/firestore';
import { DIARY_FIELDS } from '../../lib/firestore/fields';
import type { DiaryEntry } from './types';

/**
 * Android `listenEntries` — 스냅샷 문서 → DiaryEntry
 */
export function mapDiaryDocument(
  docId: string,
  data: Record<string, unknown>,
): DiaryEntry | null {
  const dateStr = data[DIARY_FIELDS.date];
  if (typeof dateStr !== 'string' || !dateStr) return null;

  const idFromField = data[DIARY_FIELDS.id];
  const id =
    typeof idFromField === 'string' && idFromField.trim() !== ''
      ? idFromField
      : docId;

  const rawBody = data[DIARY_FIELDS.body];
  const body = typeof rawBody === 'string' ? rawBody : '';

  const photos = parsePhotos(data[DIARY_FIELDS.photos]);

  return { id, date: dateStr, body, photos };
}

export function getUpdatedAtMs(data: Record<string, unknown>): number {
  const ts = data[DIARY_FIELDS.updatedAt];
  if (ts instanceof Timestamp) return ts.toMillis();
  return 0;
}

function parsePhotos(raw: unknown): string[] {
  if (!Array.isArray(raw)) return [];
  return raw
    .map((p) => (typeof p === 'string' ? p : ''))
    .filter((s) => s.trim() !== '');
}

/** listenEntries 정렬 — Android compareByDescending updatedAt → date → id */
export function sortDiaryEntries(entries: DiaryEntry[], meta: Map<string, number>): DiaryEntry[] {
  return [...entries].sort((a, b) => {
    const ua = meta.get(a.id) ?? 0;
    const ub = meta.get(b.id) ?? 0;
    if (ub !== ua) return ub - ua;
    if (b.date !== a.date) return b.date.localeCompare(a.date);
    return b.id.localeCompare(a.id);
  });
}
