import { useEffect, useMemo, useRef, useState } from 'react';
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
  selectedId?: string;
  embedded?: boolean;
  /** PC: 월 네비가 상단 헤더로 옮겨졌을 때 사이드 월 UI 숨김 */
  hideMonthNav?: boolean;
}

export function DiaryListPage({
  entries,
  loading,
  monthKey,
  onMonthChange,
  onSelect,
  onCreate,
  selectedId,
  embedded = false,
  hideMonthNav = false,
}: DiaryListPageProps) {
  const [pickerOpen, setPickerOpen] = useState(false);
  const [canScrollMore, setCanScrollMore] = useState(false);
  const bodyRef = useRef<HTMLElement>(null);

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

  const canGoNextMonth = monthOptions.includes(shiftMonthKey(monthKey, 1));

  useEffect(() => {
    if (!embedded) {
      setCanScrollMore(false);
      return;
    }
    const el = bodyRef.current;
    if (!el) return;

    const update = () => {
      setCanScrollMore(el.scrollHeight - el.scrollTop - el.clientHeight > 4);
    };

    update();
    el.addEventListener('scroll', update, { passive: true });
    const ro = new ResizeObserver(update);
    ro.observe(el);
    return () => {
      el.removeEventListener('scroll', update);
      ro.disconnect();
    };
  }, [embedded, monthEntries.length, loading, monthKey]);

  const showSideMonthNav = !hideMonthNav;

  return (
    <div
      className={
        embedded
          ? `app-list-pane${canScrollMore ? ' app-list-pane--more' : ''}`
          : 'relative min-h-dvh pb-[var(--page-pad-bottom)]'
      }
    >
      {showSideMonthNav &&
        (embedded ? null : (
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
                disabled={!canGoNextMonth}
                onClick={() => onMonthChange(shiftMonthKey(monthKey, 1))}
              >
                ›
              </IconButton>
            }
          />
        ))}

      <main
        ref={embedded ? bodyRef : undefined}
        className={embedded ? 'app-list-pane__body' : 'app-page app-page-stack'}
      >
        {loading ? (
          <LoadingView label="불러오는 중…" />
        ) : embedded ? (
          <ul className="app-list app-list--tiles">
            <li>
              <button
                type="button"
                className="app-list-item app-list-item--create"
                aria-label="새 글 작성"
                onClick={onCreate}
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                  <path
                    d="M12 5v14M5 12h14"
                    stroke="currentColor"
                    strokeWidth="1.8"
                    strokeLinecap="round"
                  />
                </svg>
              </button>
            </li>
            {monthEntries.map((entry) => (
              <li key={entry.id || entry.date}>
                <DiaryListItem
                  entry={entry}
                  selected={Boolean(selectedId && entry.id === selectedId)}
                  onClick={() => onSelect(entry)}
                />
              </li>
            ))}
          </ul>
        ) : monthEntries.length === 0 ? (
          <EmptyState
            title="이 달에는 아직 글이 없어요"
            description="오른쪽 아래 버튼으로 오늘의 한 페이지를 채워 보세요."
          />
        ) : (
          <ul className="app-list app-list--tiles app-list--tiles-mobile">
            {monthEntries.map((entry) => (
              <li key={entry.id || entry.date}>
                <DiaryListItem
                  entry={entry}
                  selected={Boolean(selectedId && entry.id === selectedId)}
                  onClick={() => onSelect(entry)}
                />
              </li>
            ))}
          </ul>
        )}
      </main>

      {embedded && <div className="app-list-pane__fade" aria-hidden="true" />}

      {!embedded && <FloatingWriteButton onClick={onCreate} />}

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
