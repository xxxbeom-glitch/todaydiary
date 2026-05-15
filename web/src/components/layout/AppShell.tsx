import type { ReactNode } from 'react';
import { cn } from '../../lib/cn';
import { debugBorder } from '../../lib/debugUi';

interface AppShellProps {
  children: ReactNode;
  className?: string;
}

export function AppShell({ children, className }: AppShellProps) {
  return (
    <div
      className={cn(
        'mx-auto min-h-dvh w-full max-w-lg text-neutral-800 md:max-w-xl',
        debugBorder(),
        className,
      )}
    >
      {children}
    </div>
  );
}
