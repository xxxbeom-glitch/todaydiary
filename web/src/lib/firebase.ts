import { initializeApp, type FirebaseApp } from 'firebase/app';
import {
  getAuth,
  GoogleAuthProvider,
  type Auth,
} from 'firebase/auth';
import { getFirestore, type Firestore } from 'firebase/firestore';
import {
  formatFirebaseEnvError,
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

function loadEnv() {
  return readFirebaseEnv();
}

function buildFirebaseConfig() {
  const env = loadEnv();
  return {
    apiKey: env.apiKey,
    authDomain: env.authDomain,
    projectId: env.projectId,
    storageBucket: env.storageBucket,
    messagingSenderId: env.messagingSenderId,
    appId: env.appId,
    databaseId: env.databaseId,
  };
}

function assertConfig(): void {
  const env = loadEnv();
  const missing = validateFirebaseEnv(env);
  if (missing.length > 0) {
    throw new Error(formatFirebaseEnvError(missing));
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
    const missing = validateFirebaseEnv(loadEnv());
    if (missing.length > 0) {
      console.error('[Firebase] missing env:', missing);
    } else {
      console.log('[Firebase] env OK, databaseId=', loadEnv().databaseId);
    }
  }
}

export function getDiaryDatabaseId(): string {
  return loadEnv().databaseId;
}

export function getFirebaseApp(): FirebaseApp {
  ensureDevLog();
  assertConfig();
  if (!app) app = initializeApp(buildFirebaseConfig());
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
  if (!diaryDb) {
    diaryDb = getFirestore(getFirebaseApp(), getDiaryDatabaseId());
  }
  return diaryDb;
}
