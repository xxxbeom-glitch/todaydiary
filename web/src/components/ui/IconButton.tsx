import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { cn } from '../../lib/cn';

interface IconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  label: string;
  children: ReactNode;
}

export function IconButton({ label, children, className, ...rest }: IconButtonProps) {
  return (
    <button
      type="button"
      aria-label={label}
      className={cn(
        'flex h-10 w-10 items-center justify-center rounded-full text-ink/80 transition-colors hover:bg-paper-warm hover:text-ink',
        className,
      )}
      {...rest}
    >
      {children}
    </button>
  );
}
