import { initializeApp, type FirebaseApp } from 'firebase/app';
import {
  getAuth,
  GoogleAuthProvider,
  type Auth,
} from 'firebase/auth';
import { getFirestore, type Firestore } from 'firebase/firestore';
import {
  logImportMetaEnvDev,
  readFirebaseEnv,
  validateFirebaseEnv,
} from './env';

/**
 * Android `FirestoreInstances` + Firebase 초기화.
 * env: `import.meta.env.VITE_*` only (not process.env).
 */
export function getFirebaseEnvForDebug() {
  logImportMetaEnvDev();
  const cfg = readFirebaseEnv();
  const missing = validateFirebaseEnv(cfg);
  return { cfg, missing, ok: missing.length === 0 };
}

const env = readFirebaseEnv();

export const DIARY_DATABASE_ID = env.databaseId;

const firebaseConfig = {
  apiKey: env.apiKey,
  authDomain: env.authDomain,
  projectId: env.projectId,
  storageBucket: env.storageBucket,
  messagingSenderId: env.messagingSenderId,
  appId: env.appId,
};

function assertConfig(): void {
  const missing = validateFirebaseEnv(env);
  if (missing.length > 0) {
    throw new Error(
      `Firebase 환경 변수가 없습니다: ${missing.join(', ')}. ` +
        '파일 위치: web/.env.local (확장자 .txt 아님). `cd web` 후 npm run dev 실행.',
    );
  }
}

let app: FirebaseApp | undefined;
let auth: Auth | undefined;
let diaryDb: Firestore | undefined;
let googleProvider: GoogleAuthProvider | undefined;
let loggedDev = false;

function ensureDevLog(): void {
  if (import.meta.env.DEV && !loggedDev) {
    loggedDev = true;
    logImportMetaEnvDev();
    const missing = validateFirebaseEnv(env);
    if (missing.length > 0) {
      console.error('[Firebase] missing env:', missing);
    } else {
      console.log('[Firebase] env OK, databaseId=', DIARY_DATABASE_ID);
    }
  }
}

export function getFirebaseApp(): FirebaseApp {
  ensureDevLog();
  assertConfig();
  if (!app) app = initializeApp(firebaseConfig);
  return app;
}

export function getFirebaseAuth(): Auth {
  getFirebaseApp();
  if (!auth) auth = getAuth();
  return auth;
}

export function getGoogleAuthProvider(): GoogleAuthProvider {
  if (!googleProvider) googleProvider = new GoogleAuthProvider();
  return googleProvider;
}

export function getDiaryFirestore(): Firestore {
  getFirebaseApp();
  if (!diaryDb) diaryDb = getFirestore(getFirebaseApp(), DIARY_DATABASE_ID);
  return diaryDb;
}
