import type { DiaryEntry } from '../../features/diary';
import { formatCardDate, previewBody } from '../../lib/date';
import { cn } from '../../lib/cn';

interface DiaryCardProps {
  entry: DiaryEntry;
  onClick: () => void;
}

export function DiaryCard({ entry, onClick }: DiaryCardProps) {
  const snippet = previewBody(entry.body) || '�??�이지';

  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        'group w-full rounded-xl border border-stone-200 bg-white px-5 py-4 text-left text-neutral-800',
        'transition-colors hover:border-stone-300 hover:bg-stone-50',
      )}
    >
      <time className="text-xs text-neutral-500">{formatCardDate(entry.date)}</time>
      <p className="mt-2 text-[15px] leading-relaxed text-neutral-800 line-clamp-3">{snippet}</p>
    </button>
  );
}
