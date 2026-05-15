import type { DiaryEntry } from './types';

/** Android `DiaryBodyDedupe.normForDedupe` */
export function normForDedupe(body: string): string {
  return body
    .replace(/\r\n/g, '\n')
    .replace(/\r/g, '\n')
    .replace(/\s+/g, ' ')
    .trim();
}

/** Android `DiaryBodyDedupe.pickOnePerDuplicateKey` */
export function pickOnePerDuplicateKey(entries: DiaryEntry[]): DiaryEntry[] {
  if (entries.length < 2) return entries;
  const groups = new Map<string, DiaryEntry[]>();
  for (const e of entries) {
    const k = `${e.date}|${normForDedupe(e.body)}`;
    const g = groups.get(k) ?? [];
    g.push(e);
    groups.set(k, g);
  }
  const out: DiaryEntry[] = [];
  for (const group of groups.values()) {
    if (group.length === 1) {
      out.push(group[0]);
    } else {
      out.push(
        group.reduce((best, cur) => {
          if (cur.photos.length > best.photos.length) return cur;
          if (cur.photos.length < best.photos.length) return best;
          return cur.id > best.id ? cur : best;
        }),
      );
    }
  }
  return out;
}
