import { useMemo, useState } from 'react';
import type { DiaryEntry } from '../features/diary';
import { DiaryListItem } from '../components/diary/DiaryListItem';
import { FloatingWriteButton } from '../components/diary/FloatingWriteButton';
import { MonthPickerModal } from '../components/diary/MonthPickerModal';
import { EmptyState } from '../components/ui/EmptyState';
import { LoadingView } from '../components/ui/LoadingView';
import { Header } from '../components/layout/Header';
import { IconButton } from '../components/ui/IconButton';
import {
  collectMonthKeys,
  entryInMonth,
  formatMonthTitle,
  shiftMonthKey,
} from '../lib/date';

interface DiaryListPageProps {
  entries: DiaryEntry[];
  loading: boolean;
  monthKey: string;
  onMonthChange: (key: string) => void;
  onSelect: (entry: DiaryEntry) => void;
  onCreate: () => void;
}

export function DiaryListPage({
  entries,
  loading,
  monthKey,
  onMonthChange,
  onSelect,
  onCreate,
}: DiaryListPageProps) {
  const [pickerOpen, setPickerOpen] = useState(false);

  const monthEntries = useMemo(
    () =>
      entries
        .filter((e) => entryInMonth(e.date, monthKey))
        .sort((a, b) => {
          if (b.date !== a.date) return b.date.localeCompare(a.date);
          return b.id.localeCompare(a.id);
        }),
    [entries, monthKey],
  );

  const monthOptions = useMemo(
    () => collectMonthKeys(entries.map((e) => e.date), monthKey),
    [entries, monthKey],
  );

  return (
    <div className="relative min-h-dvh pb-[var(--page-pad-bottom)]">
      <Header
        className="app-header--month"
        left={
          <IconButton
            label="이전 달"
            onClick={() => onMonthChange(shiftMonthKey(monthKey, -1))}
          >
            ‹
          </IconButton>
        }
        center={
          <button
            type="button"
            onClick={() => setPickerOpen(true)}
            className="type-section-title px-2 py-1"
            aria-label="월 선택"
          >
            {formatMonthTitle(monthKey)}
          </button>
        }
        right={
          <IconButton
            label="다음 달"
            onClick={() => onMonthChange(shiftMonthKey(monthKey, 1))}
          >
            ›
          </IconButton>
        }
      />

      <main className="app-page app-page-stack">
        {loading ? (
          <LoadingView label="일기를 불러오는 중…" />
        ) : monthEntries.length === 0 ? (
          <EmptyState
            title="이 달에는 아직 글이 없어요"
            description="오른쪽 아래 버튼으로 오늘의 한 페이지를 채워 보세요."
          />
        ) : (
          <ul className="app-list">
            {monthEntries.map((entry) => (
              <li key={entry.id || entry.date}>
                <DiaryListItem entry={entry} onClick={() => onSelect(entry)} />
              </li>
            ))}
          </ul>
        )}
      </main>

      <FloatingWriteButton onClick={onCreate} />

      {pickerOpen && (
        <MonthPickerModal
          value={monthKey}
          months={monthOptions}
          onChange={onMonthChange}
          onClose={() => setPickerOpen(false)}
        />
      )}
    </div>
  );
}
