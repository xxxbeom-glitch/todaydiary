import type { ReactNode } from 'react';
import { cn } from '../../lib/cn';
import { debugBorder } from '../../lib/debugUi';

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
        'sticky top-0 z-30 grid h-14 grid-cols-[3rem_1fr_3rem] items-center border-b border-stone-200 bg-stone-50/95 text-neutral-800 backdrop-blur-sm',
        debugBorder(),
        className,
      )}
    >
      <div className="flex items-center justify-start pl-1 text-neutral-800">{left}</div>
      <div className="truncate text-center text-[15px] font-medium text-neutral-800">{center}</div>
      <div className="flex items-center justify-end pr-1 text-neutral-800">{right}</div>
    </header>
  );
}
