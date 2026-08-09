export type AppTheme = 'light' | 'dark';
export type AppFont = 'griun' | 'kyobo' | 'bookk-gothic' | 'bookk-myungjo';

const THEME_KEY = 'todaydiary.theme';
const FONT_KEY = 'todaydiary.font';

export const FONT_OPTIONS: { id: AppFont; label: string; sample: string }[] = [
  { id: 'griun', label: '그리운한글', sample: '오늘 하루를 기록해요' },
  { id: 'kyobo', label: '교보손글씨', sample: '오늘 하루를 기록해요' },
  { id: 'bookk-gothic', label: '부크크고딕', sample: '오늘 하루를 기록해요' },
  { id: 'bookk-myungjo', label: '부크크명조', sample: '오늘 하루를 기록해요' },
];

const FONT_STACK: Record<AppFont, string> = {
  griun: "'GriunXHangeul', 'Pretendard', sans-serif",
  kyobo: "'KyoboHandwriting', 'Pretendard', sans-serif",
  'bookk-gothic': "'BookkGothic', 'Pretendard', sans-serif",
  'bookk-myungjo': "'BookkMyungjo', 'Pretendard', serif",
};

function isAppFont(v: string | null): v is AppFont {
  return FONT_OPTIONS.some((o) => o.id === v);
}

export function readStoredTheme(): AppTheme {
  const v = localStorage.getItem(THEME_KEY);
  return v === 'dark' ? 'dark' : 'light';
}

export function readStoredFont(): AppFont {
  const v = localStorage.getItem(FONT_KEY);
  return isAppFont(v) ? v : 'griun';
}

export function applyTheme(theme: AppTheme) {
  document.documentElement.dataset.theme = theme;
  localStorage.setItem(THEME_KEY, theme);
}

export function applyFont(font: AppFont) {
  document.documentElement.style.setProperty('--font-sans', FONT_STACK[font]);
  localStorage.setItem(FONT_KEY, font);
}

export function initPreferences() {
  applyTheme(readStoredTheme());
  applyFont(readStoredFont());
}
