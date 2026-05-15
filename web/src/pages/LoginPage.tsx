interface LoginPageProps {
  onLogin: () => void;
  loading?: boolean;
  error?: string | null;
}

export function LoginPage({ onLogin, loading, error }: LoginPageProps) {
  return (
    <div className="flex min-h-dvh flex-col items-center justify-center px-8">
      <div className="w-full max-w-sm text-center">
        <p className="text-xs tracking-[0.2em] text-ink-muted uppercase">Today Diary</p>
        <h1 className="mt-4 font-serif text-4xl font-medium tracking-tight text-ink">하루기록</h1>
        <p className="mt-6 text-sm leading-relaxed text-ink-muted">
          조용히 하루를 남기는 곳.
          <br />
          Android 앱과 같은 계정으로 이어집니다.
        </p>

        {error && (
          <p className="mt-6 rounded-lg border border-red-200/80 bg-red-50/50 px-4 py-3 text-sm text-red-800/90">
            {error}
          </p>
        )}

        <button
          type="button"
          disabled={loading}
          onClick={onLogin}
          className="mt-10 w-full rounded-xl border border-line bg-white py-3.5 text-[15px] text-ink transition-colors hover:bg-paper-warm disabled:opacity-50"
        >
          {loading ? '연결 중…' : 'Google로 계속하기'}
        </button>
      </div>
    </div>
  );
}
