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

export function formatCardDate(dateStr: string): string {
  const [, m, d] = dateStr.split('-').map(Number);
  return `${m}월 ${d}일`;
}

/** 목록 행 — 날짜 숫자 + "5월 · 일요일" */
export function formatListRowDate(dateStr: string): { day: number; sublabel: string } {
  const [, m, d] = dateStr.split('-').map(Number);
  const dt = new Date(
    Number(dateStr.slice(0, 4)),
    m - 1,
    d,
  );
  return {
    day: d,
    sublabel: `${m}월 · ${WEEKDAYS[dt.getDay()]}요일`,
  };
}

export function formatDetailDate(dateStr: string): string {
  const [y, m, d] = dateStr.split('-').map(Number);
  const dt = new Date(y, m - 1, d);
  return `${y}년 ${m}월 ${d}일 ${WEEKDAYS[dt.getDay()]}요일`;
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

export function previewBody(body: string, max = 120): string {
  const t = body.replace(/\s+/g, ' ').trim();
  if (!t) return '';
  if (t.length <= max) return t;
  return `${t.slice(0, max)}…`;
}
