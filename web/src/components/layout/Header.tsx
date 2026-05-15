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
    <header className={cn('app-header', className)}>
      <div className="flex items-center justify-start pl-1">{left}</div>
      <div className="truncate px-1 text-center type-body-strong">{center}</div>
      <div className="flex items-center justify-end pr-1">{right}</div>
    </header>
  );
}
