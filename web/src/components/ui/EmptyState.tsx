interface EmptyStateProps {
  title: string;
  description?: string;
}

export function EmptyState({ title, description }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center px-8 py-20 text-center text-neutral-800">
      <p className="text-lg text-neutral-900">{title}</p>
      {description && (
        <p className="mt-3 max-w-xs text-sm leading-relaxed text-neutral-600">{description}</p>
      )}
    </div>
  );
}
