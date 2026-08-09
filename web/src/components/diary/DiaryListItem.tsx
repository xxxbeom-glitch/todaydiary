import type { DiaryEntry } from '../../features/diary';
import { formatListDateLabel } from '../../lib/date';

interface DiaryListItemProps {
  entry: DiaryEntry;
  onClick: () => void;
  selected?: boolean;
}

export function DiaryListItem({ entry, onClick, selected = false }: DiaryListItemProps) {
  const label = formatListDateLabel(entry.date);

  return (
    <button
      type="button"
      onClick={onClick}
      className={`app-list-item w-full${selected ? ' app-list-item--selected' : ''}`}
      aria-current={selected ? 'true' : undefined}
      aria-label={`${label} 일기 보기`}
    >
      <span className="app-list-item__label">{label}</span>
    </button>
  );
}
