import { useCallback, useEffect, useState } from 'react';
import type { User } from 'firebase/auth';
import {
  signInWithGoogle,
  signOutUser,
  subscribeAuthState,
  syncUserProfileIfNeeded,
} from './authService';

export interface UseAuthResult {
  user: User | null;
  loading: boolean;
  error: string | null;
  setError: (message: string | null) => void;
  loginWithGoogle: () => Promise<void>;
  logout: () => Promise<void>;
}

/**
 * Android `rememberFirebaseUserState` + 로그인 시 `ensureUserDocument`
 */
export function useAuth(): UseAuthResult {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let unsub: (() => void) | undefined;
    try {
      unsub = subscribeAuthState((u) => {
        setUser(u);
        setLoading(false);
        void syncUserProfileIfNeeded(u).catch((e) => {
          console.error('[useAuth] profile sync', e);
          setError(
            `유저 문서 동기화 실패: ${e instanceof Error ? e.message : String(e)}`,
          );
        });
      });
    } catch (e) {
      console.error('[useAuth] subscribe failed', e);
      setLoading(false);
      setError(
        e instanceof Error ? e.message : 'Firebase Auth 초기화에 실패했습니다',
      );
    }
    return () => unsub?.();
  }, []);

  const loginWithGoogle = useCallback(async () => {
    setError(null);
    try {
      const u = await signInWithGoogle();
      setUser(u);
    } catch (e) {
      console.error('[useAuth] login', e);
      setError(
        `Google 로그인 실패: ${e instanceof Error ? e.message : String(e)}`,
      );
    }
  }, []);

  const logout = useCallback(async () => {
    setError(null);
    try {
      await signOutUser();
    } catch (e) {
      console.error('[useAuth] logout', e);
      setError(e instanceof Error ? e.message : '로그아웃에 실패했습니다');
    }
  }, []);

  return { user, loading, error, setError, loginWithGoogle, logout };
}
