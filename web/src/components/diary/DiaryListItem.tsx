import type { DiaryEntry } from '../../features/diary';
import { formatListRowDate } from '../../lib/date';

interface DiaryListItemProps {
  entry: DiaryEntry;
  onClick: () => void;
}

function listStatusLabel(entry: DiaryEntry): string {
  const hasText = entry.body.trim().length > 0;
  const hasPhotos = entry.photos.length > 0;
  if (hasText || hasPhotos) return '기록 있음';
  return '빈 페이지';
}

export function DiaryListItem({ entry, onClick }: DiaryListItemProps) {
  const { day, sublabel } = formatListRowDate(entry.date);
  const status = listStatusLabel(entry);

  return (
    <button
      type="button"
      onClick={onClick}
      className="app-list-item w-full"
      aria-label={`${sublabel} 일기 보기`}
    >
      <span className="app-list-item__day" aria-hidden="true">
        {String(day).padStart(2, '0')}
      </span>

      <span className="app-list-item__meta type-body-strong flex-1 truncate">{sublabel}</span>

      <span className="app-list-item__status type-caption shrink-0">{status}</span>

      <span className="app-list-item__chevron shrink-0" aria-hidden="true">
        <svg
          width="16"
          height="16"
          viewBox="0 0 16 16"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.5"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <path d="M6 4l4 4-4 4" />
        </svg>
      </span>
    </button>
  );
}
