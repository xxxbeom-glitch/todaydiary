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
    <div className="app-shell flex min-h-dvh flex-col items-center justify-center px-[18px] py-12">
      <div className="w-full max-w-[320px] text-center">
        <p
          className="type-caption tracking-[0.16em] uppercase"
          style={{ color: 'var(--color-text-muted)' }}
        >
          Today Diary
        </p>
        <h1 className="type-page-title mt-5">하루기록</h1>
        <p className="type-body mt-4" style={{ color: 'var(--color-text-secondary)' }}>
          조용히 하루를 남기는 곳.
          <br />
          Android 앱과 같은 계정으로 이어집니다.
        </p>

        {displayError && (
          <div className="mt-7 space-y-2.5 text-left">
            <p className="app-alert-danger">{displayError}</p>
            {envError && (
              <div className="app-alert-warn">
                <p className="type-body-strong" style={{ color: 'var(--color-text-primary)' }}>
                  배포 환경(Vercel) 설정 필요
                </p>
                <p className="type-caption mt-1.5">{firebaseEnvSetupHint()}</p>
                <ul className="type-caption mt-2 list-inside list-disc font-mono">
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
          className="app-btn app-btn-primary mt-10 w-full"
        >
          {loading ? '연결 중…' : 'Google로 계속하기'}
        </button>

        <p className="type-caption mt-8">로그인하면 Android 앱의 일기가 동기화됩니다.</p>
      </div>
    </div>
  );
}
