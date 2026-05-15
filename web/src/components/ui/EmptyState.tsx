interface EmptyStateProps {
  title: string;
  description?: string;
}

export function EmptyState({ title, description }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center px-8 py-24 text-center">
      <p className="text-[32px] leading-none">🌿</p>
      <p className="mt-5 text-[15px] text-neutral-700">{title}</p>
      {description && (
        <p className="mt-3 max-w-[260px] text-[13px] leading-6 text-neutral-500">{description}</p>
      )}
    </div>
  );
}
