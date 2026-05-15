export function LoadingView({ label = '???? ??' }: { label?: string }) {
  return (
    <div className="flex min-h-[40vh] flex-col items-center justify-center gap-3">
      <span className="h-5 w-5 animate-spin rounded-full border-2 border-stone-200 border-t-stone-500" />
      <p className="text-[13px] text-neutral-500">{label}</p>
    </div>
  );
}
