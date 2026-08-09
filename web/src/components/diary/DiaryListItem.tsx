import type { DiaryEntry } from '../../features/diary';
import { formatListTileDate } from '../../lib/date';

interface DiaryListItemProps {
  entry: DiaryEntry;
  onClick: () => void;
  selected?: boolean;
}

export function DiaryListItem({ entry, onClick, selected = false }: DiaryListItemProps) {
  const { dayLabel, weekdayLabel } = formatListTileDate(entry.date);

  return (
    <button
      type="button"
      onClick={onClick}
      className={`app-list-item${selected ? ' app-list-item--selected' : ''}`}
      aria-current={selected ? 'true' : undefined}
      aria-label={`${dayLabel} ${weekdayLabel} 일기 보기`}
    >
      <span className="app-list-item__day">{dayLabel}</span>
      <span className="app-list-item__weekday">{weekdayLabel}</span>
    </button>
  );
}
