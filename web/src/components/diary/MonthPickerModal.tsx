import { useMemo, useState } from 'react';
import { parseYearMonth, todayISO } from '../../lib/date';

interface MonthPickerModalProps {
  value: string;
  months: string[];
  onChange: (monthKey: string) => void;
  onClose: () => void;
}

const MONTH_LABELS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12] as const;

export function MonthPickerModal({ value, months, onChange, onClose }: MonthPickerModalProps) {
  const available = useMemo(() => new Set(months), [months]);
  const { year: selectedYear, month: selectedMonth } = parseYearMonth(value);
  const [viewYear, setViewYear] = useState(selectedYear);
  const todayKey = todayISO().slice(0, 7);

  const { minYear, maxYear } = useMemo(() => {
    const years = months.map((key) => Number(key.slice(0, 4)));
    return {
      minYear: Math.min(...years),
      maxYear: Math.max(...years),
    };
  }, [months]);

  const pickMonth = (month: number) => {
    const key = `${viewYear}-${String(month).padStart(2, '0')}`;
    if (!available.has(key)) return;
    onChange(key);
    onClose();
  };

  return (
    <div className="app-month-picker" role="presentation" onClick={onClose}>
      <div
        role="dialog"
        aria-modal="true"
        aria-label="월 선택"
        className="app-month-picker__panel"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="app-month-picker__header">
          <button
            type="button"
            className="app-month-picker__nav"
            aria-label="이전 해"
            disabled={viewYear <= minYear}
            onClick={() => setViewYear((y) => y - 1)}
          >
            ‹
          </button>
          <p className="app-month-picker__year">{viewYear}년</p>
          <button
            type="button"
            className="app-month-picker__nav"
            aria-label="다음 해"
            disabled={viewYear >= maxYear}
            onClick={() => setViewYear((y) => y + 1)}
          >
            ›
          </button>
        </div>

        <div className="app-month-picker__grid" role="listbox" aria-label={`${viewYear}년 월`}>
          {MONTH_LABELS.map((month) => {
            const key = `${viewYear}-${String(month).padStart(2, '0')}`;
            const enabled = available.has(key);
            const selected = viewYear === selectedYear && month === selectedMonth;
            return (
              <button
                key={month}
                type="button"
                role="option"
                aria-selected={selected}
                className={`app-month-picker__month${selected ? ' is-selected' : ''}`}
                disabled={!enabled}
                onClick={() => pickMonth(month)}
              >
                {month}월
              </button>
            );
          })}
        </div>

        <div className="app-month-picker__footer">
          <button
            type="button"
            className="app-month-picker__today"
            disabled={!available.has(todayKey)}
            onClick={() => {
              onChange(todayKey);
              onClose();
            }}
          >
            이번 달
          </button>
        </div>
      </div>
    </div>
  );
}
