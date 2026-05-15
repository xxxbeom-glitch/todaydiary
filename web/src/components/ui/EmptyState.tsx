interface EmptyStateProps {
  title: string;
  description?: string;
}

export function EmptyState({ title, description }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center px-8 py-20 text-center">
      <p className="font-serif text-lg text-ink/90">{title}</p>
      {description && (
        <p className="mt-3 max-w-xs text-sm leading-relaxed text-ink-muted">{description}</p>
      )}
    </div>
  );
}
