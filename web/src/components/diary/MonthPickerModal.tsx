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
      className="fixed inset-0 z-50 flex items-end justify-center bg-black/20 backdrop-blur-[2px] sm:items-center"
      role="presentation"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-labelledby="month-picker-title"
        className="w-full max-w-sm rounded-t-3xl border border-stone-200/60 bg-[#faf8f5] px-6 pb-8 pt-6 shadow-2xl sm:rounded-3xl"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="mb-1 h-1 w-10 rounded-full bg-stone-300 mx-auto sm:hidden" />
        <h2 id="month-picker-title" className="mt-3 text-[15px] font-medium text-neutral-800">
          월 선택
        </h2>
        <select
          value={value}
          onChange={(e) => {
            onChange(e.target.value);
            onClose();
          }}
          className="mt-4 w-full rounded-xl border border-stone-200 bg-white px-4 py-3 text-[14px] text-neutral-800 outline-none focus:border-stone-400"
        >
          {months.map((m) => (
            <option key={m} value={m}>
              {formatMonthTitle(m)}
            </option>
          ))}
        </select>
        <button
          type="button"
          onClick={onClose}
          className="mt-4 w-full rounded-xl border border-stone-200 bg-white py-3 text-[14px] text-neutral-600 hover:bg-stone-50"
        >
          닫기
        </button>
      </div>
    </div>
  );
}
