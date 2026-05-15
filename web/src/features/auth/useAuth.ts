import { useCallback, useEffect, useState } from 'react';
import type { User } from 'firebase/auth';
import {
  handleRedirectResult,
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
 * redirect 로그인 결과도 자동 처리.
 */
export function useAuth(): UseAuthResult {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // redirect 로그인 후 돌아온 경우 결과 처리 (popup-blocked 폴백)
    void handleRedirectResult().catch((e) => {
      console.error('[useAuth] redirect result:', e);
    });

    let unsub: (() => void) | undefined;
    try {
      unsub = subscribeAuthState((u) => {
        setUser(u);
        setLoading(false);
        if (u) {
          void syncUserProfileIfNeeded(u).catch((e) => {
            console.error('[useAuth] profile sync:', e);
          });
        }
      });
    } catch (e) {
      console.error('[useAuth] subscribe failed:', e);
      setLoading(false);
      setError(
        e instanceof Error
          ? e.message
          : 'Firebase Auth 초기화 실패. .env.local 값과 Firebase 콘솔 설정을 확인하세요.',
      );
    }

    return () => unsub?.();
  }, []);

  const loginWithGoogle = useCallback(async () => {
    setError(null);
    try {
      const u = await signInWithGoogle();
      // u가 null이면: 사용자가 창 닫음 또는 redirect 중 (페이지 리로드 예정)
      if (u) setUser(u);
    } catch (e) {
      console.error('[useAuth] login error:', e);
      setError(
        e instanceof Error
          ? e.message
          : 'Google 로그인에 실패했습니다. 잠시 후 다시 시도하세요.',
      );
    }
  }, []);

  const logout = useCallback(async () => {
    setError(null);
    try {
      await signOutUser();
      setUser(null);
    } catch (e) {
      console.error('[useAuth] logout error:', e);
      setError(e instanceof Error ? e.message : '로그아웃에 실패했습니다.');
    }
  }, []);

  return { user, loading, error, setError, loginWithGoogle, logout };
}
