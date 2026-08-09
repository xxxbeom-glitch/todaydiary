import { formatMonthTitle } from '../../lib/date';
import { SettingsIcon } from '../ui/SettingsIcon';

interface DesktopTopBarProps {
  monthKey: string;
  onPrevMonth: () => void;
  onNextMonth: () => void;
  onOpenMonthPicker: () => void;
  canGoNextMonth: boolean;
  onOpenSettings: () => void;
}

export function DesktopTopBar({
  monthKey,
  onPrevMonth,
  onNextMonth,
  onOpenMonthPicker,
  canGoNextMonth,
  onOpenSettings,
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
          aria-label="설정"
          onClick={onOpenSettings}
        >
          <SettingsIcon />
        </button>
      </div>
    </header>
  );
}
