export type AppTheme = 'light' | 'dark';
export type AppFont = 'pretendard' | 'system';

const THEME_KEY = 'todaydiary.theme';
const FONT_KEY = 'todaydiary.font';

const FONT_STACK: Record<AppFont, string> = {
  pretendard:
    "'Pretendard', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
  system: "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
};

export function readStoredTheme(): AppTheme {
  const v = localStorage.getItem(THEME_KEY);
  return v === 'dark' ? 'dark' : 'light';
}

export function readStoredFont(): AppFont {
  const v = localStorage.getItem(FONT_KEY);
  return v === 'system' ? 'system' : 'pretendard';
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
