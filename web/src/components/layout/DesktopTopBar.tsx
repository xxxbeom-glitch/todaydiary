interface DesktopTopBarProps {
  canDelete: boolean;
  canSave: boolean;
  onDelete: () => void;
  onSave: () => void;
}

export function DesktopTopBar({ canDelete, canSave, onDelete, onSave }: DesktopTopBarProps) {
  return (
    <header className="app-topbar">
      <div className="app-topbar__brand">
        <span className="app-topbar__mark" aria-hidden="true">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
            <path
              d="M4 14c2.5-1 4-3.2 4-5.5S7.2 4 5.5 4 3 5.2 3 7c0 2.8 2.2 5.5 5 7 2.8-1.5 5-4.2 5-7 0-1.8-1.2-3-2.5-3S8 5.2 8 7c0 2.3 1.5 4.5 4 5.5"
              stroke="currentColor"
              strokeWidth="1.4"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </span>
        <span className="app-topbar__logo">하루기록</span>
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
