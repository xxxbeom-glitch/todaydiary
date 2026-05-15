import { useEffect, useState } from 'react';
import { subscribeDiaryEntries } from './diaryRepository';
import { pickOnePerDuplicateKey } from './diaryBodyDedupe';
import type { DiaryEntry } from './types';

export interface UseDiariesResult {
  entries: DiaryEntry[];
  count: number;
  ready: boolean;
  error: string | null;
  setError: (message: string | null) => void;
}

function friendlyFirestoreError(e: Error): string {
  const msg = e.message ?? '';
  if (msg.includes('permission-denied') || msg.includes('PERMISSION_DENIED')) {
    return (
      'Firestore 읽기 권한이 없습니다. Firebase Console → Firestore → ' +
      'diary 데이터베이스 → 규칙(Rules) 탭에서 보안 규칙을 설정하세요. ' +
      '(web/firestore.rules 파일 참고)'
    );
  }
  if (msg.includes('unavailable') || msg.includes('UNAVAILABLE')) {
    return '네트워크 연결을 확인하세요. Firestore에 접근할 수 없습니다.';
  }
  if (msg.includes('not-found') || msg.includes('NOT_FOUND')) {
    return (
      '`diary` Firestore 데이터베이스가 없습니다. ' +
      'Firebase Console → Firestore → 데이터베이스 추가 → 이름을 `diary`로 설정하세요.'
    );
  }
  return `데이터 로드 실패: ${msg}`;
}

/** `users/{uid}/diaries` 실시간 구독 + Android dedupe */
export function useDiaries(uid: string | undefined): UseDiariesResult {
  const [entries, setEntries] = useState<DiaryEntry[]>([]);
  const [ready, setReady] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!uid) {
      setEntries([]);
      setReady(false);
      setError(null);
      return;
    }

    setReady(false);
    const unsub = subscribeDiaryEntries(
      uid,
      (list) => {
        setEntries(pickOnePerDuplicateKey(list));
        setReady(true);
        setError(null);
      },
      (e) => {
        console.error('[useDiaries] snapshot error:', e);
        setError(friendlyFirestoreError(e));
        setReady(true);
      },
    );

    return () => unsub();
  }, [uid]);

  return {
    entries,
    count: entries.length,
    ready,
    error,
    setError,
  };
}
