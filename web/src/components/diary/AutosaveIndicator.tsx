import type { AutosaveStatus } from '../../hooks/useDiaryEditor';
import { cn } from '../../lib/cn';

const LABEL: Record<AutosaveStatus, string | null> = {
  idle: null,
  saving: '?? ?',
  saved: '???',
  error: '?? ??',
};

interface AutosaveIndicatorProps {
  status: AutosaveStatus;
  className?: string;
}

export function AutosaveIndicator({ status, className }: AutosaveIndicatorProps) {
  const label = LABEL[status];
  if (!label) return null;

  return (
    <span
      className={cn('type-caption', className)}
      style={{
        color: status === 'error' ? 'var(--color-danger)' : 'var(--color-text-muted)',
      }}
    >
      {label}
    </span>
  );
}
