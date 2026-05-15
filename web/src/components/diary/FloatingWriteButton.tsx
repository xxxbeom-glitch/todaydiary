interface FloatingWriteButtonProps {
  onClick: () => void;
}

export function FloatingWriteButton({ onClick }: FloatingWriteButtonProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      aria-label="새 일기 쓰기"
      className="fixed bottom-6 right-6 z-40 flex h-14 w-14 items-center justify-center rounded-full bg-accent text-2xl font-light text-white shadow-sm transition-transform hover:scale-[1.02] active:scale-[0.98] md:bottom-10 md:right-[max(1.5rem,calc(50%-14rem))]"
    >
      +
    </button>
  );
}
