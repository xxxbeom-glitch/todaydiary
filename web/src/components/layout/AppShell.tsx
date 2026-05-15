import type { ReactNode } from 'react';
import { cn } from '../../lib/cn';

interface AppShellProps {
  children: ReactNode;
  className?: string;
}

export function AppShell({ children, className }: AppShellProps) {
  return (
    <div
      className={cn(
        'mx-auto min-h-dvh w-full max-w-lg bg-[#faf8f5] md:max-w-xl',
        className,
      )}
    >
      {children}
    </div>
  );
}
