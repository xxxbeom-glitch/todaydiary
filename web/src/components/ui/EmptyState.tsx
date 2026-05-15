interface EmptyStateProps {
  title: string;
  description?: string;
}

export function EmptyState({ title, description }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center px-4 py-20 text-center">
      <span className="text-[28px] leading-none opacity-80" aria-hidden="true">
        ◦
      </span>
      <p className="type-section-title mt-5">{title}</p>
      {description && (
        <p className="type-caption mt-3 max-w-[260px]" style={{ color: 'var(--color-text-secondary)' }}>
          {description}
        </p>
      )}
    </div>
  );
}
