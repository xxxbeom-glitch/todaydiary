import { useEffect, useRef, type ReactNode } from 'react';
import { cn } from '../../lib/cn';

/** 커서 추적 그림자 최대 오프셋(px). 기본 y는 CSS --shadow-shell-y(8) */
const SHADOW_FOLLOW = 14;

interface AppShellProps {
  children: ReactNode;
  className?: string;
  /** PC: 셸 뒤에 한 겹 더 보이는 외곽 프레임 */
  framed?: boolean;
}

export function AppShell({ children, className, framed = false }: AppShellProps) {
  const outerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!framed) return;
    const el = outerRef.current;
    if (!el) return;

    const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (reduceMotion) return;

    const onMove = (e: MouseEvent) => {
      const rect = el.getBoundingClientRect();
      const nx = (e.clientX - (rect.left + rect.width / 2)) / (rect.width / 2);
      const ny = (e.clientY - (rect.top + rect.height / 2)) / (rect.height / 2);
      const x = Math.max(-1, Math.min(1, nx)) * SHADOW_FOLLOW;
      const y = 8 + Math.max(-1, Math.min(1, ny)) * SHADOW_FOLLOW;
      el.style.setProperty('--shadow-shell-x', `${x.toFixed(1)}px`);
      el.style.setProperty('--shadow-shell-y', `${y.toFixed(1)}px`);
    };

    window.addEventListener('mousemove', onMove, { passive: true });
    return () => window.removeEventListener('mousemove', onMove);
  }, [framed]);

  const shell = <div className={cn('app-shell', className)}>{children}</div>;
  if (!framed) return shell;
  return (
    <div ref={outerRef} className="app-shell-outer">
      {shell}
    </div>
  );
}
