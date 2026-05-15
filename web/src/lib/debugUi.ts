/** 디버그 border — 개발 중 레이아웃 확인용. 기본 false */
export const DEBUG_UI_BORDERS = false;

export function debugBorder(className = ''): string {
  return DEBUG_UI_BORDERS ? `border-2 border-red-500 ${className}`.trim() : className;
}
