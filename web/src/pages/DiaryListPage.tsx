import { useEffect, useMemo, useRef, useState } from 'react';
import type { DiaryEntry } from '../features/diary';
import { DiaryListItem } from '../components/diary/DiaryListItem';
import { FloatingWriteButton } from '../components/diary/FloatingWriteButton';
import { MonthPickerModal } from '../components/diary/MonthPickerModal';
import { LoadingView } from '../components/ui/LoadingView';
import { Header } from '../components/layout/Header';
import { IconButton } from '../components/ui/IconButton';
import {
  collectMonthKeys,
  entryInMonth,
  formatMonthTitle,
  shiftMonthKey,
  yearMonthKey,
} from '../lib/date';

interface DiaryListPageProps {
  entries: DiaryEntry[];
  loading: boolean;
  monthKey: string;
  onMonthChange: (key: string) => void;
  onSelect: (entry: DiaryEntry) => void;
  onCreate: () => void;
  selectedId?: string;
  /** PC 좌측 세로 레일 */
  embedded?: boolean;
  /** 모바일 상단 가로 레일 */
  rail?: boolean;
  /** PC: 월 네비가 상단 헤더로 옮겨졌을 때 사이드 월 UI 숨김 */
  hideMonthNav?: boolean;
  onOpenSettings?: () => void;
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
  rail = false,
  hideMonthNav = false,
  onOpenSettings,
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
  const showCreateTile = embedded && monthKey === yearMonthKey();
  const showCreateFab = rail && monthKey === yearMonthKey();

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

  const showMonthNav = !hideMonthNav;

  const listItems =
    monthEntries.length === 0 ? (
      <p className={rail ? 'app-list-rail__empty type-caption' : 'app-list-pane__empty type-caption'}>
        글 없음
      </p>
    ) : (
      <ul className={rail ? 'app-list app-list--rail' : 'app-list app-list--tiles'}>
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
    );

  if (rail) {
    return (
      <div className="app-list-rail">
        {showMonthNav && (
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
              <div className="app-list-rail__header-right">
                <IconButton
                  label="다음 달"
                  disabled={!canGoNextMonth}
                  onClick={() => onMonthChange(shiftMonthKey(monthKey, 1))}
                >
                  ›
                </IconButton>
                {onOpenSettings && (
                  <IconButton label="설정" onClick={onOpenSettings}>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                      <path
                        d="M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"
                        stroke="currentColor"
                        strokeWidth="1.6"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      />
                      <path
                        d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.5V19a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H5a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3H10a1.7 1.7 0 0 0 1-1.5V5a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8V10c.1.7.7 1.2 1.5 1.2H19a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1Z"
                        stroke="currentColor"
                        strokeWidth="1.6"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      />
                    </svg>
                  </IconButton>
                )}
              </div>
            }
          />
        )}

        <div className="app-list-rail__scroller">
          {loading ? <LoadingView label="불러오는 중…" /> : listItems}
        </div>

        {showCreateFab && <FloatingWriteButton onClick={onCreate} />}

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

  return (
    <div
      className={`app-list-pane${canScrollMore ? ' app-list-pane--more' : ''}${showCreateTile ? ' app-list-pane--create' : ''}`}
    >
      <main ref={bodyRef} className="app-list-pane__body">
        {loading ? <LoadingView label="불러오는 중…" /> : listItems}
      </main>

      <div className="app-list-pane__fade" aria-hidden="true" />

      {showCreateTile && (
        <div className="app-list-pane__dock">
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
        </div>
      )}

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
