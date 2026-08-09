import { formatMonthTitle } from '../../lib/date';

interface DesktopTopBarProps {
  monthKey: string;
  onPrevMonth: () => void;
  onNextMonth: () => void;
  onOpenMonthPicker: () => void;
  canGoNextMonth: boolean;
  canDelete: boolean;
  onDelete: () => void;
}

export function DesktopTopBar({
  monthKey,
  onPrevMonth,
  onNextMonth,
  onOpenMonthPicker,
  canGoNextMonth,
  canDelete,
  onDelete,
}: DesktopTopBarProps) {
  return (
    <header className="app-topbar">
      <div className="app-topbar__side" aria-hidden="true" />

      <div className="app-topbar__month">
        <button
          type="button"
          className="app-topbar__month-nav"
          aria-label="이전 달"
          onClick={onPrevMonth}
        >
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true">
            <path
              d="M10 3.5 5.5 8 10 12.5"
              stroke="currentColor"
              strokeWidth="1.6"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </button>
        <button
          type="button"
          className="app-topbar__month-label"
          aria-label="월 선택"
          onClick={onOpenMonthPicker}
        >
          {formatMonthTitle(monthKey)}
        </button>
        <button
          type="button"
          className="app-topbar__month-nav"
          aria-label="다음 달"
          disabled={!canGoNextMonth}
          onClick={onNextMonth}
        >
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true">
            <path
              d="M6 3.5 10.5 8 6 12.5"
              stroke="currentColor"
              strokeWidth="1.6"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </button>
      </div>

      <div className="app-topbar__actions">
        <button
          type="button"
          className="app-topbar__icon-btn"
          aria-label="삭제"
          disabled={!canDelete}
          onClick={onDelete}
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path
              d="M4 7h16M10 11v6M14 11v6M6 7l1 12a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2l1-12M9 7V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"
              stroke="currentColor"
              strokeWidth="1.6"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </button>
      </div>
    </header>
  );
}
