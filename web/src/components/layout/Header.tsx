import type { ReactNode } from 'react';
import { cn } from '../../lib/cn';

interface HeaderProps {
  left?: ReactNode;
  center?: ReactNode;
  right?: ReactNode;
  className?: string;
}

export function Header({ left, center, right, className }: HeaderProps) {
  return (
    <header
      className={cn(
        'sticky top-0 z-30 grid h-14 grid-cols-[3rem_1fr_3rem] items-center border-b border-line bg-paper/95 backdrop-blur-sm',
        className,
      )}
    >
      <div className="flex items-center justify-start pl-1">{left}</div>
      <div className="truncate text-center text-[15px] font-medium text-ink">{center}</div>
      <div className="flex items-center justify-end pr-1">{right}</div>
    </header>
  );
}
