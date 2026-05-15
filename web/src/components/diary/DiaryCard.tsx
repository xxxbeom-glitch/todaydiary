import type { DiaryEntry } from '../../features/diary';
import { previewBody } from '../../lib/date';
import { cn } from '../../lib/cn';

interface DiaryCardProps {
  entry: DiaryEntry;
  onClick: () => void;
}

const WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'] as const;

function cardDateLabel(dateStr: string): string {
  const [y, m, d] = dateStr.split('-').map(Number);
  const dt = new Date(y, m - 1, d);
  return `${m}월 ${d}일 ${WEEKDAYS[dt.getDay()]}`;
}

export function DiaryCard({ entry, onClick }: DiaryCardProps) {
  const snippet = previewBody(entry.body) || '(빈 페이지)';
  const hasPhotos = Array.isArray(entry.photos) && entry.photos.length > 0;

  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        'group w-full rounded-2xl border border-stone-200/80 bg-white px-5 py-4 text-left',
        'shadow-[0_1px_4px_rgba(0,0,0,0.04)]',
        'transition-all hover:border-stone-300 hover:shadow-[0_2px_10px_rgba(0,0,0,0.07)]',
        'active:scale-[0.99]',
      )}
    >
      <div className="flex items-center justify-between">
        <time className="text-[11px] tracking-wide text-neutral-400">
          {cardDateLabel(entry.date)}
        </time>
        {hasPhotos && (
          <span className="text-[11px] text-neutral-400">
            사진 {entry.photos.length}
          </span>
        )}
      </div>
      <p className="mt-2.5 text-[14px] leading-7 text-neutral-700 line-clamp-3">{snippet}</p>
    </button>
  );
}
