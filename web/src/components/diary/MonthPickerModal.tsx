import { useMemo } from 'react';
import { DayPicker } from 'react-day-picker';
import { ko } from 'react-day-picker/locale';
import { parseYearMonth, todayISO, yearMonthKey } from '../../lib/date';

interface MonthPickerModalProps {
  value: string;
  months: string[];
  onChange: (monthKey: string) => void;
  onClose: () => void;
}

export function MonthPickerModal({ value, months, onChange, onClose }: MonthPickerModalProps) {
  const available = useMemo(() => new Set(months), [months]);
  const { year, month } = parseYearMonth(value);
  const selected = new Date(year, month - 1, 1);
  const today = todayISO();

  return (
    <div className="app-month-picker" role="presentation" onClick={onClose}>
      <div
        role="dialog"
        aria-modal="true"
        aria-label="월 선택"
        className="app-month-picker__panel app-date-picker"
        onClick={(e) => e.stopPropagation()}
      >
        <DayPicker
          mode="single"
          locale={ko}
          weekStartsOn={0}
          selected={selected}
          defaultMonth={selected}
          disabled={(date) => !available.has(yearMonthKey(date))}
          onSelect={(day) => {
            if (!day) return;
            onChange(yearMonthKey(day));
            onClose();
          }}
          footer={
            <div className="app-date-picker__footer">
              <button
                type="button"
                className="app-date-picker__today"
                disabled={!available.has(today.slice(0, 7))}
                onClick={() => {
                  onChange(today.slice(0, 7));
                  onClose();
                }}
              >
                이번 달
              </button>
            </div>
          }
        />
      </div>
    </div>
  );
}
