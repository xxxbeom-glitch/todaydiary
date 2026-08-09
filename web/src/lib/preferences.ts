export type AppTheme = 'light' | 'dark';
export type AppFont = 'pretendard' | 'kyobo' | 'bookk-gothic' | 'bookk-myungjo';
export type AppFontSize = 'sm' | 'md' | 'lg';
/** 뷰포트 레이아웃별 설정 저장 키 */
export type PrefLayout = 'desktop' | 'mobile';

const THEME_KEY = 'todaydiary.theme';
const FONT_KEY = 'todaydiary.font';
const FONT_SIZE_KEY = 'todaydiary.fontSize';
const DESKTOP_MQ = '(min-width: 1024px)';

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

export function layoutFromViewport(): PrefLayout {
  if (typeof window === 'undefined') return 'mobile';
  return window.matchMedia(DESKTOP_MQ).matches ? 'desktop' : 'mobile';
}

function scopedKey(base: string, layout: PrefLayout): string {
  return `${base}.${layout}`;
}

/** 레이아웃 키 없으면 예전 공통 키 → 없으면 기본값 */
function readLegacyOrDefault(
  base: string,
  layout: PrefLayout,
  parse: (v: string | null) => string,
): string {
  const scoped = localStorage.getItem(scopedKey(base, layout));
  if (scoped != null) return parse(scoped);
  const legacy = localStorage.getItem(base);
  if (legacy != null) {
    localStorage.setItem(scopedKey(base, layout), legacy);
    return parse(legacy);
  }
  return parse(null);
}

export function readStoredTheme(layout: PrefLayout = layoutFromViewport()): AppTheme {
  const v = readLegacyOrDefault(THEME_KEY, layout, (x) => x ?? '');
  return v === 'dark' ? 'dark' : 'light';
}

export function readStoredFont(layout: PrefLayout = layoutFromViewport()): AppFont {
  const v = readLegacyOrDefault(FONT_KEY, layout, (x) => x ?? '');
  return isAppFont(v) ? v : 'pretendard';
}

export function readStoredFontSize(layout: PrefLayout = layoutFromViewport()): AppFontSize {
  const v = readLegacyOrDefault(FONT_SIZE_KEY, layout, (x) => x ?? '');
  return isAppFontSize(v) ? v : 'md';
}

export function applyTheme(theme: AppTheme, layout: PrefLayout = layoutFromViewport()) {
  document.documentElement.dataset.theme = theme;
  localStorage.setItem(scopedKey(THEME_KEY, layout), theme);
}

export function applyFont(font: AppFont, layout: PrefLayout = layoutFromViewport()) {
  document.documentElement.dataset.font = font;
  document.documentElement.style.setProperty('--font-sans', UI_FONT_STACK);
  document.documentElement.style.setProperty('--font-prose', FONT_STACK[font]);
  localStorage.setItem(scopedKey(FONT_KEY, layout), font);
}

export function applyFontSize(size: AppFontSize, layout: PrefLayout = layoutFromViewport()) {
  document.documentElement.dataset.fontSize = size;
  document.documentElement.style.setProperty('--font-size-prose', FONT_SIZE_PX[size]);
  localStorage.setItem(scopedKey(FONT_SIZE_KEY, layout), size);
}

export function applyPreferences(layout: PrefLayout) {
  document.documentElement.style.setProperty('--font-sans', UI_FONT_STACK);
  applyTheme(readStoredTheme(layout), layout);
  applyFont(readStoredFont(layout), layout);
  applyFontSize(readStoredFontSize(layout), layout);
}

export function initPreferences() {
  applyPreferences(layoutFromViewport());
}
