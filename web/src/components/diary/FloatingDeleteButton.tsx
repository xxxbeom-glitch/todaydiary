interface FloatingDeleteButtonProps {
  onClick: () => void;
}

export function FloatingDeleteButton({ onClick }: FloatingDeleteButtonProps) {
  return (
    <button
      type="button"
      className="app-fab-delete"
      aria-label="삭제"
      onClick={onClick}
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
  );
}
