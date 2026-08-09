import { useEffect, useId, useRef, useState } from 'react';
import { DayPicker } from 'react-day-picker';
import { ko } from 'react-day-picker/locale';
import { formatEntryDateLabel, parseISODate, toISO, todayISO } from '../../lib/date';

interface DatePickerFieldProps {
  value: string;
  onChange: (iso: string) => void;
  /** 선택 가능 최대일 (기본: 오늘) */
  max?: string;
}

export function DatePickerField({
  value,
  onChange,
  max = todayISO(),
}: DatePickerFieldProps) {
  const [open, setOpen] = useState(false);
  const rootRef = useRef<HTMLDivElement>(null);
  const dialogId = useId();

  useEffect(() => {
    if (!open) return;

    const onPointerDown = (e: PointerEvent) => {
      if (!rootRef.current?.contains(e.target as Node)) setOpen(false);
    };
    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setOpen(false);
    };

    document.addEventListener('pointerdown', onPointerDown);
    document.addEventListener('keydown', onKeyDown);
    return () => {
      document.removeEventListener('pointerdown', onPointerDown);
      document.removeEventListener('keydown', onKeyDown);
    };
  }, [open]);

  const selected = parseISODate(value);
  const maxDate = parseISODate(max);

  return (
    <div ref={rootRef} className="app-date-picker">
      <button
        type="button"
        className="app-entry-date app-date-picker__trigger"
        aria-haspopup="dialog"
        aria-expanded={open}
        aria-controls={dialogId}
        onClick={() => setOpen((v) => !v)}
      >
        {formatEntryDateLabel(value)}
      </button>

      {open && (
        <div
          id={dialogId}
          role="dialog"
          aria-label="날짜 선택"
          className="app-date-picker__popover"
        >
          <DayPicker
            mode="single"
            locale={ko}
            weekStartsOn={0}
            selected={selected}
            defaultMonth={selected}
            disabled={{ after: maxDate }}
            onSelect={(day) => {
              if (!day) return;
              onChange(toISO(day));
              setOpen(false);
            }}
            footer={
              <div className="app-date-picker__footer">
                <button
                  type="button"
                  className="app-date-picker__today"
                  onClick={() => {
                    onChange(todayISO());
                    setOpen(false);
                  }}
                >
                  오늘
                </button>
              </div>
            }
          />
        </div>
      )}
    </div>
  );
}
