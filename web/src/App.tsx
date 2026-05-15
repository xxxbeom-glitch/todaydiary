/**
 * 임시 테스트 UI — Firebase Auth + Firestore `users/{uid}/diaries` 연동 확인
 */
import type { CSSProperties } from 'react';
import { useAuth } from './features/auth';
import { useDiaries } from './features/diary';

export default function App() {
  const { user, loading, error: authError, loginWithGoogle, logout } = useAuth();
  const { count, ready, error: diaryError } = useDiaries(user?.uid);

  if (loading) {
    return <p style={styles.muted}>인증 상태 확인 중…</p>;
  }

  return (
    <main style={styles.main}>
      <h1 style={styles.h1}>하루기록 Web — 연동 테스트</h1>
      <p style={styles.muted}>Firestore DB: diary · 경로: users/&#123;uid&#125;/diaries</p>

      {authError && <p style={styles.error}>Auth: {authError}</p>}
      {diaryError && <p style={styles.error}>Firestore: {diaryError}</p>}

      {!user ? (
        <button type="button" style={styles.btn} onClick={() => void loginWithGoogle()}>
          Google 로그인
        </button>
      ) : (
        <section style={styles.card}>
          <dl style={styles.dl}>
            <dt>UID</dt>
            <dd style={styles.mono}>{user.uid}</dd>
            <dt>이메일</dt>
            <dd>{user.email ?? '(없음)'}</dd>
            <dt>일기 개수</dt>
            <dd>{ready ? `${count}건` : '불러오는 중…'}</dd>
          </dl>
          <button type="button" style={styles.btnSecondary} onClick={() => void logout()}>
            로그아웃
          </button>
        </section>
      )}
    </main>
  );
}

const styles: Record<string, CSSProperties> = {
  main: { fontFamily: 'system-ui, sans-serif', padding: 24, maxWidth: 480, margin: '0 auto' },
  h1: { fontSize: 20, fontWeight: 600, margin: '0 0 8px' },
  muted: { color: '#666', fontSize: 14, margin: '0 0 16px' },
  error: { color: '#b00020', fontSize: 14 },
  btn: {
    padding: '10px 20px',
    fontSize: 15,
    cursor: 'pointer',
    border: 'none',
    borderRadius: 6,
    background: '#1a73e8',
    color: '#fff',
  },
  btnSecondary: {
    marginTop: 16,
    padding: '8px 16px',
    fontSize: 14,
    cursor: 'pointer',
    border: '1px solid #ccc',
    borderRadius: 6,
    background: '#fff',
  },
  card: {
    border: '1px solid #e0e0e0',
    borderRadius: 8,
    padding: 16,
  },
  dl: { margin: 0, display: 'grid', gridTemplateColumns: '88px 1fr', gap: '8px 12px' },
  mono: { fontFamily: 'ui-monospace, monospace', fontSize: 13, wordBreak: 'break-all' },
};
