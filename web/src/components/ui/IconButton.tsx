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
        'flex h-10 w-10 items-center justify-center rounded-full text-neutral-700 transition-colors hover:bg-stone-200 hover:text-neutral-900',
        className,
      )}
      {...rest}
    >
      {children}
    </button>
  );
}
