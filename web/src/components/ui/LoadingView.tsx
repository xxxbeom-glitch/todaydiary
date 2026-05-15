export function LoadingView({ label = '불러오는 중…' }: { label?: string }) {
  return (
    <div
      className="flex min-h-[40vh] flex-col items-center justify-center gap-3 text-neutral-600"
    >
      <span className="h-5 w-5 animate-spin rounded-full border-2 border-stone-300 border-t-stone-700" />
      <p className="text-sm font-medium text-neutral-800">{label}</p>
    </div>
  );
}
