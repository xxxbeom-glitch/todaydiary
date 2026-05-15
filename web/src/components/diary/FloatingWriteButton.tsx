interface FloatingWriteButtonProps {
  onClick: () => void;
}

export function FloatingWriteButton({ onClick }: FloatingWriteButtonProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      aria-label="일기 쓰기"
      className="fixed bottom-7 right-5 z-40 flex h-14 w-14 items-center justify-center rounded-full bg-neutral-800 text-white shadow-lg transition-all hover:bg-neutral-700 active:scale-95 md:bottom-10 md:right-[max(1.5rem,calc(50%-22rem))]"
    >
      <svg
        width="20"
        height="20"
        viewBox="0 0 20 20"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
      >
        <path d="M14.5 2.5a2.121 2.121 0 0 1 3 3L6 17l-4 1 1-4L14.5 2.5z" />
      </svg>
    </button>
  );
}
