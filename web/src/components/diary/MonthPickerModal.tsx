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
      className="fixed inset-0 z-50 flex items-end justify-center bg-[#2b2721]/20 backdrop-blur-[2px]"
      role="presentation"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-labelledby="month-picker-title"
        className="w-full max-w-[var(--app-max-width)] rounded-t-[22px] border border-b-0 border-[var(--color-border)] bg-[var(--color-surface)] px-[18px] pb-8 pt-5"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="mx-auto mb-4 h-1 w-10 rounded-full bg-[var(--color-border)]" />
        <h2 id="month-picker-title" className="type-section-title">
          월 선택
        </h2>
        <select
          value={value}
          onChange={(e) => {
            onChange(e.target.value);
            onClose();
          }}
          className="app-input mt-4"
        >
          {months.map((m) => (
            <option key={m} value={m}>
              {formatMonthTitle(m)}
            </option>
          ))}
        </select>
        <button type="button" onClick={onClose} className="app-btn app-btn-primary mt-3 w-full">
          닫기
        </button>
      </div>
    </div>
  );
}
