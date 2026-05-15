/** UI 렌더 영역 확인용 — 문제 해결 후 false 로 변경 */
export const DEBUG_UI_BORDERS = true;

export function debugBorder(className = ''): string {
  return DEBUG_UI_BORDERS ? `border-2 border-red-500 ${className}`.trim() : className;
}
