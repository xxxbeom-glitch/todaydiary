import {
  FIREBASE_ENV_KEYS,
  firebaseEnvSetupHint,
  formatFirebaseEnvError,
  readFirebaseEnv,
  validateFirebaseEnv,
} from '../lib/env';

interface LoginPageProps {
  onLogin: () => void;
  loading?: boolean;
  error?: string | null;
}

export function LoginPage({ onLogin, loading, error }: LoginPageProps) {
  const envMissing = validateFirebaseEnv(readFirebaseEnv());
  const envError = envMissing.length > 0 ? formatFirebaseEnvError(envMissing) : null;
  const displayError = envError ?? error;
  const loginDisabled = loading || envMissing.length > 0;

  return (
    <div className="flex min-h-dvh flex-col items-center justify-center bg-[#faf8f5] px-8">
      <div className="w-full max-w-sm text-center">
        <p className="text-[11px] tracking-[0.18em] text-neutral-400 uppercase">Today Diary</p>
        <h1 className="mt-5 text-[2.6rem] font-light tracking-tight text-neutral-800">
          하루기록
        </h1>
        <p className="mt-5 text-[13px] leading-7 text-neutral-500">
          조용히 하루를 남기는 곳.
          <br />
          Android 앱과 같은 계정으로 이어집니다.
        </p>

        {displayError && (
          <div className="mt-7 space-y-2.5 text-left">
            <p className="rounded-xl border border-red-100 bg-red-50 px-4 py-3 text-[13px] text-red-700">
              {displayError}
            </p>
            {envError && (
              <div className="rounded-xl border border-amber-100 bg-amber-50 px-4 py-3 text-[12px] leading-relaxed text-amber-900">
                <p className="font-medium">배포 환경(Vercel) 설정 필요</p>
                <p className="mt-1.5 text-amber-800">{firebaseEnvSetupHint()}</p>
                <ul className="mt-2 list-inside list-disc font-mono text-[11px] text-amber-700">
                  {FIREBASE_ENV_KEYS.map((k) => (
                    <li key={k}>{k}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}

        <button
          type="button"
          disabled={loginDisabled}
          onClick={onLogin}
          className="mt-10 w-full rounded-2xl border border-stone-200 bg-white py-4 text-[14px] text-neutral-800 shadow-sm transition-colors hover:bg-stone-50 active:bg-stone-100 disabled:opacity-40"
        >
          {loading ? '연결 중…' : 'Google로 계속하기'}
        </button>

        <p className="mt-8 text-[11px] text-neutral-400">
          로그인하면 Android 앱의 일기가 동기화됩니다.
        </p>
      </div>
    </div>
  );
}
