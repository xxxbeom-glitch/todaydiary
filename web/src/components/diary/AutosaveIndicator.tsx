import type { AutosaveStatus } from '../../hooks/useDiaryEditor';
import { cn } from '../../lib/cn';

const LABEL: Record<AutosaveStatus, string | null> = {
  idle: null,
  saving: '저장 중…',
  saved: '저장됨',
  error: '저장 실패',
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
      className={cn(
        'text-xs tracking-wide',
        status === 'error' ? 'text-red-700/80' : 'text-ink-muted',
        className,
      )}
    >
      {label}
    </span>
  );
}
