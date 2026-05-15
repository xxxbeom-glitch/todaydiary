export type { UserProfilePayload } from './types';
export { ensureUserDocument } from './userRepository';
export {
  signInWithGoogle,
  signOutUser,
  subscribeAuthState,
  syncUserProfileIfNeeded,
} from './authService';
export { useAuth } from './useAuth';
export type { UseAuthResult } from './useAuth';
