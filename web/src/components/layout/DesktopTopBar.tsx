import { formatMonthTitle } from '../../lib/date';

interface DesktopTopBarProps {
  monthKey: string;
  onPrevMonth: () => void;
  onNextMonth: () => void;
  onOpenMonthPicker: () => void;
  canDelete: boolean;
  canSave: boolean;
  onDelete: () => void;
  onSave: () => void;
}

export function DesktopTopBar({
  monthKey,
  onPrevMonth,
  onNextMonth,
  onOpenMonthPicker,
  canDelete,
  canSave,
  onDelete,
  onSave,
}: DesktopTopBarProps) {
  return (
    <header className="app-topbar">
      <div className="app-topbar__month">
        <button
          type="button"
          className="app-topbar__month-nav"
          aria-label="이전 달"
          onClick={onPrevMonth}
        >
          ‹
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
          onClick={onNextMonth}
        >
          ›
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
        <button
          type="button"
          className="app-topbar__icon-btn"
          aria-label="저장"
          disabled={!canSave}
          onClick={onSave}
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path
              d="M5 3h11l3 3v15a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z"
              stroke="currentColor"
              strokeWidth="1.6"
              strokeLinejoin="round"
            />
            <path d="M8 3v6h7V3" stroke="currentColor" strokeWidth="1.6" strokeLinejoin="round" />
            <path d="M8 17h8" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" />
          </svg>
        </button>
      </div>
    </header>
  );
}
