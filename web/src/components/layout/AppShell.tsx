import type { ReactNode } from 'react';
import { cn } from '../../lib/cn';

interface AppShellProps {
  children: ReactNode;
  className?: string;
}

export function AppShell({ children, className }: AppShellProps) {
  return <div className={cn('app-shell', className)}>{children}</div>;
}
