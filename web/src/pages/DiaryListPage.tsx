import { useMemo, useState } from 'react';
import type { DiaryEntry } from '../features/diary';
import { DiaryCard } from '../components/diary/DiaryCard';
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
  onLogout: () => void;
}

export function DiaryListPage({
  entries,
  loading,
  monthKey,
  onMonthChange,
  onSelect,
  onCreate,
  onLogout,
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
    <div className="relative min-h-dvh pb-24">
      <Header
        left={
          <button
            type="button"
            onClick={onLogout}
            className="ml-1 rounded-lg px-2 py-1 text-xs text-neutral-500 hover:bg-stone-100 hover:text-neutral-900"
          >
            나가기
          </button>
        }
        center={
          <button
            type="button"
            onClick={() => setPickerOpen(true)}
            className="inline-flex items-center gap-1  text-[16px] tracking-tight"
          >
            {formatMonthTitle(monthKey)}
            <span className="text-xs text-neutral-500">▾</span>
          </button>
        }
        right={
          <div className="flex gap-0.5">
            <IconButton
              label="이전 달"
              onClick={() => onMonthChange(shiftMonthKey(monthKey, -1))}
            >
              ‹
            </IconButton>
            <IconButton
              label="다음 달"
              onClick={() => onMonthChange(shiftMonthKey(monthKey, 1))}
            >
              ›
            </IconButton>
          </div>
        }
      />

      <main className="px-4 py-6 md:px-6">
        {loading ? (
          <LoadingView label="일기를 불러오는 중…" />
        ) : monthEntries.length === 0 ? (
          <EmptyState
            title="이 달에는 아직 글이 없어요"
            description="오른쪽 아래 버튼으로 오늘의 한 페이지를 채워 보세요."
          />
        ) : (
          <ul className="flex flex-col gap-3">
            {monthEntries.map((entry) => (
              <li key={entry.id || entry.date}>
                <DiaryCard entry={entry} onClick={() => onSelect(entry)} />
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
