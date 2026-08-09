const WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'] as const;

export function todayISO(): string {
  const n = new Date();
  return toISO(n);
}

export function toISO(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

/** `YYYY-MM-DD` → 로컬 Date (UTC 파싱 시차 방지) */
export function parseISODate(iso: string): Date {
  const [y, m, d] = iso.split('-').map(Number);
  return new Date(y, m - 1, d);
}

export function yearMonthKey(d: Date = new Date()): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  return `${y}-${m}`;
}

export function parseYearMonth(key: string): { year: number; month: number } {
  const [ys, ms] = key.split('-');
  return { year: Number(ys), month: Number(ms) };
}

export function formatMonthTitle(key: string): string {
  const { year, month } = parseYearMonth(key);
  return `${year}년 ${month}월`;
}

export function entryInMonth(date: string, monthKey: string): boolean {
  return date.startsWith(`${monthKey}-`);
}

/** 본문 상단 날짜 — "2026년 8월 9일 (일)" */
export function formatEntryDateLabel(dateStr: string): string {
  const [y, m, d] = dateStr.split('-').map(Number);
  const dt = new Date(y, m - 1, d);
  return `${y}년 ${m}월 ${d}일 (${WEEKDAYS[dt.getDay()]})`;
}

/** 목록 타일 — "9일" / "일요일" */
export function formatListTileDate(dateStr: string): { dayLabel: string; weekdayLabel: string } {
  const [, , d] = dateStr.split('-').map(Number);
  const dt = new Date(Number(dateStr.slice(0, 4)), Number(dateStr.slice(5, 7)) - 1, d);
  return {
    dayLabel: `${d}일`,
    weekdayLabel: `${WEEKDAYS[dt.getDay()]}요일`,
  };
}

export function shiftMonthKey(key: string, delta: number): string {
  const { year, month } = parseYearMonth(key);
  const d = new Date(year, month - 1 + delta, 1);
  return yearMonthKey(d);
}

export function collectMonthKeys(dates: string[], current: string): string[] {
  const set = new Set<string>();
  for (const d of dates) set.add(d.slice(0, 7));
  set.add(current);
  set.add(yearMonthKey());
  return [...set].sort();
}
