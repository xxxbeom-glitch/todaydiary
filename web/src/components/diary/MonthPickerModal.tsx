import { formatMonthTitle } from '../../lib/date';
import { debugBorder } from '../../lib/debugUi';

interface MonthPickerModalProps {
  value: string;
  months: string[];
  onChange: (monthKey: string) => void;
  onClose: () => void;
}

export function MonthPickerModal({ value, months, onChange, onClose }: MonthPickerModalProps) {
  return (
    <div
      className="fixed inset-0 z-50 flex items-end justify-center bg-black/20 p-4 sm:items-center"
      role="presentation"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-labelledby="month-picker-title"
        className={`w-full max-w-sm rounded-2xl border border-stone-200 bg-stone-50 p-6 text-neutral-800 shadow-lg ${debugBorder()}`}
        onClick={(e) => e.stopPropagation()}
      >
        <h2 id="month-picker-title" className="text-lg font-semibold text-neutral-900">
          ???�택
        </h2>
        <select
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="mt-4 w-full rounded-lg border border-stone-300 bg-white px-3 py-2.5 text-[15px] text-neutral-900 outline-none"
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
            className="rounded-lg px-4 py-2 text-sm text-neutral-600 hover:bg-stone-200"
          >
            취소
          </button>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg bg-stone-700 px-4 py-2 text-sm text-white"
          >
            ?�인
          </button>
        </div>
      </div>
    </div>
  );
}
