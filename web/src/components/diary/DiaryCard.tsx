import type { DiaryEntry } from '../../features/diary';
import { previewBody } from '../../lib/date';

const WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'] as const;

function cardDateLabel(dateStr: string): string {
  const [y, m, d] = dateStr.split('-').map(Number);
  const dt = new Date(y, m - 1, d);
  return `${m}월 ${d}일 ${WEEKDAYS[dt.getDay()]}`;
}

interface DiaryCardProps {
  entry: DiaryEntry;
  onClick: () => void;
}

export function DiaryCard({ entry, onClick }: DiaryCardProps) {
  const snippet = previewBody(entry.body) || '(빈 페이지)';
  const hasPhotos = Array.isArray(entry.photos) && entry.photos.length > 0;

  return (
    <button
      type="button"
      onClick={onClick}
      className="app-card w-full text-left transition-[border-color] active:border-[var(--color-accent)]"
    >
      <div className="flex items-center justify-between gap-2">
        <time className="type-caption" style={{ color: 'var(--color-text-muted)' }}>
          {cardDateLabel(entry.date)}
        </time>
        {hasPhotos && (
          <span className="type-caption" style={{ color: 'var(--color-text-muted)' }}>
            사진 {entry.photos.length}
          </span>
        )}
      </div>
      <p className="type-body mt-3 line-clamp-3" style={{ color: 'var(--color-text-secondary)' }}>
        {snippet}
      </p>
    </button>
  );
}
