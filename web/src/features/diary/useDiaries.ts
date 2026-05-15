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
        setError(`데이터 로드 실패: ${e.message}`);
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
