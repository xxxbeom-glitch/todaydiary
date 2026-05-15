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
  userName?: string;
}

export function DiaryListPage({
  entries,
  loading,
  monthKey,
  onMonthChange,
  onSelect,
  onCreate,
  onLogout,
  userName,
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

  const userInitial = userName ? userName.charAt(0).toUpperCase() : '?';

  return (
    <div className="relative min-h-dvh pb-28">
      <Header
        left={
          <button
            type="button"
            onClick={() => setPickerOpen(true)}
            className="flex items-center gap-1 px-1 py-1 text-[15px] font-medium text-neutral-800"
            aria-label="월 선택"
          >
            {formatMonthTitle(monthKey)}
            <svg
              width="12"
              height="12"
              viewBox="0 0 12 12"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
              strokeLinecap="round"
              strokeLinejoin="round"
              className="text-neutral-400"
              aria-hidden="true"
            >
              <path d="M2 4l4 4 4-4" />
            </svg>
          </button>
        }
        right={
          <div className="flex items-center gap-1">
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

      {/* 유저 정보 바 */}
      <div className="flex items-center justify-between border-b border-stone-200/50 px-5 py-2.5">
        <div className="flex items-center gap-2.5">
          <div className="flex h-7 w-7 items-center justify-center rounded-full bg-stone-200 text-[11px] font-medium text-neutral-600">
            {userInitial}
          </div>
          {userName && (
            <span className="max-w-[160px] truncate text-[12px] text-neutral-500">{userName}</span>
          )}
        </div>
        <button
          type="button"
          onClick={onLogout}
          className="rounded-lg px-2.5 py-1 text-[12px] text-neutral-500 hover:bg-stone-100 hover:text-neutral-800"
        >
          로그아웃
        </button>
      </div>

      <main className="px-4 py-5 md:px-6">
        {loading ? (
          <LoadingView label="일기를 불러오는 중…" />
        ) : monthEntries.length === 0 ? (
          <EmptyState
            title="이 달에는 아직 글이 없어요"
            description="오른쪽 아래 버튼으로 오늘의 한 페이지를 채워 보세요."
          />
        ) : (
          <ul className="flex flex-col gap-2.5">
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
