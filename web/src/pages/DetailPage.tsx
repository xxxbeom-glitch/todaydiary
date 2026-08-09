import type { DiaryEntry } from '../features/diary';
import { IconButton } from '../components/ui/IconButton';
import { formatEntryDateLabel } from '../lib/date';

interface DetailPageProps {
  entry: DiaryEntry;
  onBack: () => void;
  onEdit: () => void;
  embedded?: boolean;
}

export function DetailPage({ entry, onBack, onEdit, embedded = false }: DetailPageProps) {
  const dateLabel = formatEntryDateLabel(entry.date);

  return (
    <div className={embedded ? 'app-pane' : 'flex min-h-dvh flex-col'}>
      <article
        className="app-page app-page-prose app-prose-fill"
        onDoubleClick={onEdit}
        title="더블클릭하여 수정"
      >
        {!embedded && (
          <div className="mb-2">
            <IconButton label="뒤로" onClick={onBack}>
              ←
            </IconButton>
          </div>
        )}

        <p className="app-entry-date">{dateLabel}</p>

        <div
          className="app-read-body app-read-body--editable"
          role="button"
          tabIndex={0}
          aria-label={`${dateLabel} 일기 본문, 더블클릭하면 수정`}
          onKeyDown={(e) => {
            if (e.key === 'Enter' || e.key === ' ') {
              e.preventDefault();
              onEdit();
            }
          }}
        >
          {entry.body.trim() || (
            <span style={{ color: 'var(--color-text-muted)' }}>내용이 없는 일기입니다.</span>
          )}
        </div>
      </article>
    </div>
  );
}
