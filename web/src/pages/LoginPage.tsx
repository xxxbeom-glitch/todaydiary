import { debugBorder } from '../lib/debugUi';
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
  const envError =
    envMissing.length > 0 ? formatFirebaseEnvError(envMissing) : null;
  const displayError = envError ?? error;
  const loginDisabled = loading || envMissing.length > 0;
  return (
    <div
      className={`flex min-h-dvh flex-col items-center justify-center bg-stone-50 px-8 text-neutral-800 ${debugBorder()}`}
    >
      <p className="text-sm font-medium text-red-600">[UI] login screen visible</p>
      <div className={`mt-6 w-full max-w-sm text-center ${debugBorder()}`}>
        <p className="text-xs tracking-widest text-neutral-500 uppercase">Today Diary</p>
        <h1 className="mt-4 text-4xl font-semibold text-neutral-900">하루기록</h1>
        <p className="mt-6 text-sm leading-relaxed text-neutral-600">
          조용히 하루를 남기는 곳.
          <br />
          Android 앱과 같은 계정으로 이어집니다.
        </p>

        {displayError && (
          <div className="mt-6 space-y-3 text-left">
            <p className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">
              {displayError}
            </p>
            {envError && (
              <div className="rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-xs leading-relaxed text-amber-950">
                <p className="font-medium">Vercel 배포 시 필수</p>
                <p className="mt-2">{firebaseEnvSetupHint()}</p>
                <ul className="mt-2 list-inside list-disc font-mono text-[11px]">
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
          className="mt-10 w-full rounded-xl border border-stone-300 bg-white py-3.5 text-[15px] font-medium text-neutral-900 shadow-sm hover:bg-stone-100 disabled:opacity-50"
        >
          {loading ? '연결 중…' : 'Google로 계속하기'}
        </button>
      </div>
    </div>
  );
}
