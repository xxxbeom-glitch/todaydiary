import type { DiaryEntry } from '../../features/diary';
import { formatCardDate, previewBody } from '../../lib/date';
import { cn } from '../../lib/cn';

interface DiaryCardProps {
  entry: DiaryEntry;
  onClick: () => void;
}

export function DiaryCard({ entry, onClick }: DiaryCardProps) {
  const snippet = previewBody(entry.body) || '빈 페이지';

  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        'group w-full rounded-xl border border-line bg-white/60 px-5 py-4 text-left',
        'transition-colors hover:border-line hover:bg-white',
      )}
    >
      <time className="text-xs tracking-wide text-ink-muted">{formatCardDate(entry.date)}</time>
      <p className="mt-2 font-serif text-[15px] leading-relaxed text-ink/90 line-clamp-3">
        {snippet}
      </p>
    </button>
  );
}
