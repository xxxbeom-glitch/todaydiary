/**
 * Android `FirestoreUserRepository.ensureUserDocument` payload
 */
export interface UserProfilePayload {
  uid: string;
  email: string;
  displayName: string;
  photoUrl: string;
  updatedAt: unknown;
  lastLoginAt: unknown;
  createdAt?: unknown;
}
