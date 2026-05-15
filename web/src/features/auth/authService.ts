import {
  onAuthStateChanged,
  signInWithPopup,
  signOut,
  type User,
  type Unsubscribe,
} from 'firebase/auth';
import { getFirebaseAuth, getGoogleAuthProvider } from '../../lib/firebase';
import { ensureUserDocument } from './userRepository';

/** Android MainActivity — Google Sign-In → Firebase Auth */
export async function signInWithGoogle(): Promise<User> {
  const result = await signInWithPopup(
    getFirebaseAuth(),
    getGoogleAuthProvider(),
  );
  await ensureUserDocument(result.user);
  return result.user;
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
