export type AppTheme = 'light' | 'dark';
export type AppFont = 'pretendard' | 'kyobo' | 'bookk-gothic' | 'bookk-myungjo';
export type AppFontSize = 'sm' | 'md' | 'lg';

const THEME_KEY = 'todaydiary.theme';
const FONT_KEY = 'todaydiary.font';
const FONT_SIZE_KEY = 'todaydiary.fontSize';

/** UI 크롬용 — 항상 Pretendard */
export const UI_FONT_STACK =
  "'Pretendard', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif";

export const FONT_OPTIONS: { id: AppFont; label: string; sample: string }[] = [
  { id: 'pretendard', label: 'Pretendard', sample: '오늘 하루를 기록해요' },
  { id: 'kyobo', label: '교보손글씨', sample: '오늘 하루를 기록해요' },
  { id: 'bookk-gothic', label: '부크크고딕', sample: '오늘 하루를 기록해요' },
  { id: 'bookk-myungjo', label: '부크크명조', sample: '오늘 하루를 기록해요' },
];

export const FONT_SIZE_OPTIONS: { id: AppFontSize; label: string }[] = [
  { id: 'sm', label: '작게' },
  { id: 'md', label: '중간' },
  { id: 'lg', label: '크게' },
];

const FONT_STACK: Record<AppFont, string> = {
  pretendard: UI_FONT_STACK,
  kyobo: "'KyoboHandwriting', 'Pretendard', sans-serif",
  'bookk-gothic': "'BookkGothic', 'Pretendard', sans-serif",
  'bookk-myungjo': "'BookkMyungjo', 'Pretendard', serif",
};

const FONT_SIZE_PX: Record<AppFontSize, string> = {
  sm: '14px',
  md: '15px',
  lg: '18px',
};

function isAppFont(v: string | null): v is AppFont {
  return FONT_OPTIONS.some((o) => o.id === v);
}

function isAppFontSize(v: string | null): v is AppFontSize {
  return FONT_SIZE_OPTIONS.some((o) => o.id === v);
}

export function readStoredTheme(): AppTheme {
  const v = localStorage.getItem(THEME_KEY);
  return v === 'dark' ? 'dark' : 'light';
}

export function readStoredFont(): AppFont {
  const v = localStorage.getItem(FONT_KEY);
  return isAppFont(v) ? v : 'pretendard';
}

export function readStoredFontSize(): AppFontSize {
  const v = localStorage.getItem(FONT_SIZE_KEY);
  return isAppFontSize(v) ? v : 'md';
}

export function applyTheme(theme: AppTheme) {
  document.documentElement.dataset.theme = theme;
  localStorage.setItem(THEME_KEY, theme);
}

export function applyFont(font: AppFont) {
  document.documentElement.dataset.font = font;
  document.documentElement.style.setProperty('--font-sans', UI_FONT_STACK);
  document.documentElement.style.setProperty('--font-prose', FONT_STACK[font]);
  localStorage.setItem(FONT_KEY, font);
}

export function applyFontSize(size: AppFontSize) {
  document.documentElement.dataset.fontSize = size;
  document.documentElement.style.setProperty('--font-size-prose', FONT_SIZE_PX[size]);
  localStorage.setItem(FONT_SIZE_KEY, size);
}

export function initPreferences() {
  document.documentElement.style.setProperty('--font-sans', UI_FONT_STACK);
  applyTheme(readStoredTheme());
  applyFont(readStoredFont());
  applyFontSize(readStoredFontSize());
}
