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
        'sticky top-0 z-30 grid h-14 grid-cols-[3rem_1fr_3rem] items-center border-b border-stone-200/70 bg-[#faf8f5]/95 backdrop-blur-sm',
        className,
      )}
    >
      <div className="flex items-center justify-start pl-2">{left}</div>
      <div className="truncate text-center text-[15px] font-medium text-neutral-800">{center}</div>
      <div className="flex items-center justify-end pr-2">{right}</div>
    </header>
  );
}
