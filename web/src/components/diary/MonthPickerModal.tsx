import { formatMonthTitle } from '../../lib/date';

interface MonthPickerModalProps {
  value: string;
  months: string[];
  onChange: (monthKey: string) => void;
  onClose: () => void;
}

export function MonthPickerModal({ value, months, onChange, onClose }: MonthPickerModalProps) {
  return (
    <div
      className="fixed inset-0 z-50 flex items-end justify-center bg-ink/20 p-4 sm:items-center"
      role="presentation"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-labelledby="month-picker-title"
        className="w-full max-w-sm rounded-2xl border border-line bg-paper p-6 shadow-sm"
        onClick={(e) => e.stopPropagation()}
      >
        <h2 id="month-picker-title" className="font-serif text-lg text-ink">
          월 선택
        </h2>
        <select
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="mt-4 w-full rounded-lg border border-line bg-white px-3 py-2.5 text-[15px] text-ink outline-none focus:border-accent/40"
        >
          {months.map((m) => (
            <option key={m} value={m}>
              {formatMonthTitle(m)}
            </option>
          ))}
        </select>
        <div className="mt-5 flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg px-4 py-2 text-sm text-ink-muted hover:bg-paper-warm"
          >
            취소
          </button>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg bg-accent px-4 py-2 text-sm text-white"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  );
}
