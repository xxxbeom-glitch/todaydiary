interface ConfirmDialogProps {
  title: string;
  description?: string;
  confirmLabel?: string;
  cancelLabel?: string;
  danger?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export function ConfirmDialog({
  title,
  description,
  confirmLabel = '확인',
  cancelLabel = '취소',
  danger = false,
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  return (
    <div
      className="app-confirm"
      role="presentation"
      onClick={onCancel}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="app-confirm-title"
        className="app-confirm__panel"
        onClick={(e) => e.stopPropagation()}
      >
        <h2 id="app-confirm-title" className="type-section-title">
          {title}
        </h2>
        {description && (
          <p className="app-confirm__desc type-body">{description}</p>
        )}
        <div className="app-confirm__actions">
          <button type="button" className="app-btn app-btn-ghost" onClick={onCancel}>
            {cancelLabel}
          </button>
          <button
            type="button"
            className={`app-btn ${danger ? 'app-btn-danger' : 'app-btn-primary'}`}
            onClick={onConfirm}
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
