export function LoadingView({ label = '불러오는 중…' }: { label?: string }) {
  return (
    <div className="flex min-h-[40vh] flex-col items-center justify-center gap-3 text-ink-muted">
      <span className="h-5 w-5 animate-spin rounded-full border-2 border-line border-t-accent" />
      <p className="text-sm tracking-wide">{label}</p>
    </div>
  );
}
