import {
  getRedirectResult,
  onAuthStateChanged,
  signInWithPopup,
  signInWithRedirect,
  signOut,
  type User,
  type Unsubscribe,
} from 'firebase/auth';
import { getFirebaseAuth, getGoogleAuthProvider } from '../../lib/firebase';
import { ensureUserDocument } from './userRepository';

/**
 * Google 로그인.
 * popup 우선, 팝업 차단 시 redirect 방식으로 자동 전환.
 * - popup 성공 → User 반환
 * - redirect 전환 → null (페이지가 리로드됨, onAuthStateChanged 가 재처리)
 * - 사용자가 창을 닫은 경우 → null (에러 아님)
 */
export async function signInWithGoogle(): Promise<User | null> {
  const auth = getFirebaseAuth();
  const provider = getGoogleAuthProvider();

  try {
    const result = await signInWithPopup(auth, provider);
    await ensureUserDocument(result.user);
    return result.user;
  } catch (e: unknown) {
    const code = (e as { code?: string }).code;

    if (code === 'auth/popup-blocked') {
      // 브라우저가 팝업을 막은 경우 → redirect 방식으로 전환
      await signInWithRedirect(auth, provider);
      return null;
    }

    if (
      code === 'auth/popup-closed-by-user' ||
      code === 'auth/cancelled-popup-request'
    ) {
      // 사용자가 직접 닫은 경우 — 에러가 아님
      return null;
    }

    if (code === 'auth/operation-not-allowed') {
      throw new Error(
        'Google 로그인이 비활성화되어 있습니다. Firebase Console → Authentication → Sign-in method → Google을 활성화하세요.',
      );
    }

    throw e;
  }
}

/**
 * redirect 로그인 후 돌아왔을 때 결과를 처리.
 * 앱 마운트 시 1회 호출.
 */
export async function handleRedirectResult(): Promise<User | null> {
  try {
    const result = await getRedirectResult(getFirebaseAuth());
    if (result?.user) {
      await ensureUserDocument(result.user);
      return result.user;
    }
    return null;
  } catch (e: unknown) {
    const code = (e as { code?: string }).code;
    if (code !== 'auth/no-current-user') {
      console.error('[auth] redirect result error:', code ?? e);
    }
    return null;
  }
}

export async function signOutUser(): Promise<void> {
  await signOut(getFirebaseAuth());
}

/** Android `rememberFirebaseUserState` — AuthStateListener */
export function subscribeAuthState(
  onUser: (user: User | null) => void,
): Unsubscribe {
  return onAuthStateChanged(getFirebaseAuth(), onUser);
}

export async function syncUserProfileIfNeeded(user: User | null): Promise<void> {
  if (user) await ensureUserDocument(user);
}
