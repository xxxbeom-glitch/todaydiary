/**
 * Vite env — 반드시 `web/.env.local` + `VITE_` 접두사.
 * `npm run dev` / `npm run build` 는 **web/** 디렉터리에서 실행해야 합니다.
 */

const RAW = import.meta.env;

/** 개발 시 브라우저 콘솔에서 실제 주입 값 확인 (키만 마스킹) */
export function logImportMetaEnvDev(): void {
  if (!import.meta.env.DEV) return;

  const keys = Object.keys(RAW).filter((k) => k.startsWith('VITE_'));
  console.log('[import.meta.env] mode=', import.meta.env.MODE, 'keys=', keys);

  const mask = (v: string | undefined) => {
    if (!v) return '(empty)';
    if (v.length <= 8) return '***';
    return `${v.slice(0, 4)}…${v.slice(-4)}`;
  };

  console.log('[Firebase env]', {
    VITE_FIREBASE_API_KEY: mask(RAW.VITE_FIREBASE_API_KEY),
    VITE_FIREBASE_AUTH_DOMAIN: RAW.VITE_FIREBASE_AUTH_DOMAIN || '(empty)',
    VITE_FIREBASE_PROJECT_ID: RAW.VITE_FIREBASE_PROJECT_ID || '(empty)',
    VITE_FIREBASE_STORAGE_BUCKET: RAW.VITE_FIREBASE_STORAGE_BUCKET || '(empty)',
    VITE_FIREBASE_MESSAGING_SENDER_ID: RAW.VITE_FIREBASE_MESSAGING_SENDER_ID || '(empty)',
    VITE_FIREBASE_APP_ID: mask(RAW.VITE_FIREBASE_APP_ID),
    VITE_FIREBASE_DATABASE_ID: RAW.VITE_FIREBASE_DATABASE_ID || '(empty)',
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
  let databaseId = trim(RAW.VITE_FIREBASE_DATABASE_ID) || 'diary';

  // Firebase 콘솔 Analytics ID(G-xxx)를 넣은 경우가 많음 → named DB 이름은 보통 `diary`
  if (databaseId.startsWith('G-')) {
    console.warn(
      '[env] VITE_FIREBASE_DATABASE_ID가 Analytics ID(G-…)처럼 보입니다. Firestore named DB 이름(예: diary)으로 바꿔 주세요.',
    );
    databaseId = 'diary';
  }

  return {
    apiKey: trim(RAW.VITE_FIREBASE_API_KEY),
    authDomain: trim(RAW.VITE_FIREBASE_AUTH_DOMAIN),
    projectId: trim(RAW.VITE_FIREBASE_PROJECT_ID),
    storageBucket: trim(RAW.VITE_FIREBASE_STORAGE_BUCKET),
    messagingSenderId: trim(RAW.VITE_FIREBASE_MESSAGING_SENDER_ID),
    appId: trim(RAW.VITE_FIREBASE_APP_ID),
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
