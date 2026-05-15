import { initializeApp, type FirebaseApp } from 'firebase/app';
import {
  getAuth,
  GoogleAuthProvider,
  type Auth,
} from 'firebase/auth';
import { getFirestore, type Firestore } from 'firebase/firestore';

/**
 * Android `FirestoreInstances` + Firebase 초기화.
 * @see app/.../data/FirestoreInstances.kt — DIARY_DATABASE_ID = "diary"
 * @see app/.../auth/FirebaseAuthState.kt — FirebaseAuth.getInstance()
 */
export const DIARY_DATABASE_ID =
  import.meta.env.VITE_FIREBASE_DATABASE_ID ?? 'diary';

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
};

function assertConfig(): void {
  if (!firebaseConfig.apiKey || !firebaseConfig.projectId) {
    throw new Error(
      'Firebase 설정이 없습니다. web/.env.local에 VITE_* 값을 채우세요. (docs/setup.md)',
    );
  }
}

let app: FirebaseApp | undefined;
let auth: Auth | undefined;
let diaryDb: Firestore | undefined;
let googleProvider: GoogleAuthProvider | undefined;

/** `initializeApp` — 단일 Firebase 앱 인스턴스 */
export function getFirebaseApp(): FirebaseApp {
  assertConfig();
  if (!app) app = initializeApp(firebaseConfig);
  return app;
}

/** Android `FirebaseAuth.getInstance()` */
export function getFirebaseAuth(): Auth {
  getFirebaseApp();
  if (!auth) auth = getAuth();
  return auth;
}

/** Android MainActivity — Google Sign-In / `GoogleAuthProvider` */
export function getGoogleAuthProvider(): GoogleAuthProvider {
  if (!googleProvider) {
    googleProvider = new GoogleAuthProvider();
  }
  return googleProvider;
}

/** Android `FirestoreInstances.diary` — `getFirestore(app, 'diary')` */
export function getDiaryFirestore(): Firestore {
  getFirebaseApp();
  if (!diaryDb) diaryDb = getFirestore(getFirebaseApp(), DIARY_DATABASE_ID);
  return diaryDb;
}
