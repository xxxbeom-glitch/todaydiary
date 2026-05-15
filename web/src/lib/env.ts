/**
 * Vite env — `import.meta.env.VITE_*` 를 **직접** 참조해야 빌드 시 치환됩니다.
 * `const x = import.meta.env; x.VITE_*` 형태는 production/preview 에서 비어 있을 수 있습니다.
 *
 * `npm run dev` / `npm run build` 는 **web/** 에서 실행.
 */

/** 개발 시 브라우저 콘솔에서 실제 주입 값 확인 (키만 마스킹) */
export function logImportMetaEnvDev(): void {
  if (!import.meta.env.DEV) return;

  const keys = Object.keys(import.meta.env).filter((k) => k.startsWith('VITE_'));
  console.log('[import.meta.env] mode=', import.meta.env.MODE, 'keys=', keys);

  const mask = (v: string | undefined) => {
    if (!v) return '(empty)';
    if (v.length <= 8) return '***';
    return `${v.slice(0, 4)}…${v.slice(-4)}`;
  };

  console.log('[Firebase env]', {
    VITE_FIREBASE_API_KEY: mask(import.meta.env.VITE_FIREBASE_API_KEY),
    VITE_FIREBASE_AUTH_DOMAIN: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || '(empty)',
    VITE_FIREBASE_PROJECT_ID: import.meta.env.VITE_FIREBASE_PROJECT_ID || '(empty)',
    VITE_FIREBASE_STORAGE_BUCKET: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || '(empty)',
    VITE_FIREBASE_MESSAGING_SENDER_ID:
      import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || '(empty)',
    VITE_FIREBASE_APP_ID: mask(import.meta.env.VITE_FIREBASE_APP_ID),
    VITE_FIREBASE_DATABASE_ID: import.meta.env.VITE_FIREBASE_DATABASE_ID || '(empty)',
  });
}

function trim(v: string | undefined): string {
  return (v ?? '').trim();
}

export interface FirebaseEnvConfig {
  apiKey: string;
  authDomain: string;
  projectId: string;
  storageBucket: string;
  messagingSenderId: string;
  appId: string;
  databaseId: string;
}

export function readFirebaseEnv(): FirebaseEnvConfig {
  let databaseId = trim(import.meta.env.VITE_FIREBASE_DATABASE_ID) || 'diary';

  if (databaseId.startsWith('G-')) {
    console.warn(
      '[env] VITE_FIREBASE_DATABASE_ID가 Analytics ID(G-…)처럼 보입니다. Firestore named DB 이름(예: diary)으로 바꿔 주세요.',
    );
    databaseId = 'diary';
  }

  return {
    apiKey: trim(import.meta.env.VITE_FIREBASE_API_KEY),
    authDomain: trim(import.meta.env.VITE_FIREBASE_AUTH_DOMAIN),
    projectId: trim(import.meta.env.VITE_FIREBASE_PROJECT_ID),
    storageBucket: trim(import.meta.env.VITE_FIREBASE_STORAGE_BUCKET),
    messagingSenderId: trim(import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID),
    appId: trim(import.meta.env.VITE_FIREBASE_APP_ID),
    databaseId,
  };
}

export function validateFirebaseEnv(cfg: FirebaseEnvConfig): string[] {
  const missing: string[] = [];
  if (!cfg.apiKey) missing.push('VITE_FIREBASE_API_KEY');
  if (!cfg.authDomain) missing.push('VITE_FIREBASE_AUTH_DOMAIN');
  if (!cfg.projectId) missing.push('VITE_FIREBASE_PROJECT_ID');
  if (!cfg.appId) missing.push('VITE_FIREBASE_APP_ID');
  return missing;
}

export const FIREBASE_ENV_KEYS = [
  'VITE_FIREBASE_API_KEY',
  'VITE_FIREBASE_AUTH_DOMAIN',
  'VITE_FIREBASE_PROJECT_ID',
  'VITE_FIREBASE_STORAGE_BUCKET',
  'VITE_FIREBASE_MESSAGING_SENDER_ID',
  'VITE_FIREBASE_APP_ID',
  'VITE_FIREBASE_DATABASE_ID',
] as const;

export function firebaseEnvSetupHint(): string {
  if (import.meta.env.DEV) {
    return (
      'web/.env.local 확인 후 터미널에서 cd web && npm run dev 실행. ' +
      '예전 dev 서버(5173 등)가 떠 있으면 종료 후 다시 실행하세요. ' +
      'npm run preview 는 build 이후에만 사용합니다.'
    );
  }
  return (
    'Vercel → Settings → Environment Variables에 web/.env.local 과 동일한 VITE_* 7개를 등록한 뒤 Redeploy 하세요.'
  );
}

export function formatFirebaseEnvError(missing: string[]): string {
  return `Firebase 환경 변수가 없습니다: ${missing.join(', ')}. ${firebaseEnvSetupHint()}`;
}
