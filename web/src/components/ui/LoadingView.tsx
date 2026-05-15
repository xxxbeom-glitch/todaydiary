export function LoadingView({ label = '???? ??' }: { label?: string }) {
  return (
    <div className="flex min-h-[40vh] flex-col items-center justify-center gap-3 py-12">
      <span className="app-spinner" aria-hidden="true" />
      <p className="type-caption" style={{ color: 'var(--color-text-secondary)' }}>
        {label}
      </p>
    </div>
  );
}
