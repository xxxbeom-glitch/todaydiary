import type { ReactNode } from 'react';
import { cn } from '../../lib/cn';

interface AppShellProps {
  children: ReactNode;
  className?: string;
  /** PC: 셸 뒤에 한 겹 더 보이는 외곽 프레임 */
  framed?: boolean;
}

export function AppShell({ children, className, framed = false }: AppShellProps) {
  const shell = <div className={cn('app-shell', className)}>{children}</div>;
  if (!framed) return shell;
  return <div className="app-shell-outer">{shell}</div>;
}
