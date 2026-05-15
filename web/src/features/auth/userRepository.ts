import { doc, getDoc, serverTimestamp, setDoc } from 'firebase/firestore';
import type { User } from 'firebase/auth';
import { getDiaryFirestore } from '../../lib/firebase';
import { USER_FIELDS } from '../../lib/firestore/fields';
import { FIRESTORE } from '../../lib/firestore/paths';

/**
 * Android `FirestoreUserRepository.ensureUserDocument`
 */
export async function ensureUserDocument(user: User): Promise<void> {
  const uid = user.uid;
  const ref = doc(getDiaryFirestore(), FIRESTORE.users, uid);
  const snap = await getDoc(ref);
  const now = serverTimestamp();

  const base: Record<string, unknown> = {
    [USER_FIELDS.uid]: uid,
    [USER_FIELDS.email]: user.email ?? '',
    [USER_FIELDS.displayName]: user.displayName ?? '',
    [USER_FIELDS.photoUrl]: user.photoURL ?? '',
    [USER_FIELDS.updatedAt]: now,
    [USER_FIELDS.lastLoginAt]: now,
  };

  const payload = snap.exists()
    ? base
    : { ...base, [USER_FIELDS.createdAt]: now };

  await setDoc(ref, payload, { merge: true });
}
