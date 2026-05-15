import { useCallback, useEffect, useRef, useState } from 'react';
import {
  createDiaryEntry,
  deleteDiaryEntry,
  updateDiaryEntry,
  type DiaryEntry,
} from '../features/diary';

export type AutosaveStatus = 'idle' | 'saving' | 'saved' | 'error';

const DEBOUNCE_MS = 900;

interface UseDiaryEditorOptions {
  uid: string;
  entryId: string;
  date: string;
  initialBody: string;
  isNew: boolean;
  photos: string[];
}

export function useDiaryEditor({
  uid,
  entryId,
  date,
  initialBody,
  isNew,
  photos,
}: UseDiaryEditorOptions) {
  const [body, setBody] = useState(initialBody);
  const [status, setStatus] = useState<AutosaveStatus>('idle');
  const [error, setError] = useState<string | null>(null);
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const photosRef = useRef(photos);
  photosRef.current = photos;

  useEffect(() => {
    setBody(initialBody);
    setStatus('idle');
    setError(null);
  }, [entryId, initialBody]);

  const persist = useCallback(
    async (text: string) => {
      setStatus('saving');
      setError(null);
      const entry: DiaryEntry = {
        id: entryId,
        date,
        body: text,
        photos: photosRef.current,
      };

      try {
        if (text.trim() === '') {
          if (isNew) {
            await deleteDiaryEntry(uid, entryId, date);
          }
          setStatus('idle');
          return;
        }
        if (isNew) {
          await createDiaryEntry(uid, entry, new Date());
        } else {
          await updateDiaryEntry(uid, entry, new Date());
        }
        setStatus('saved');
      } catch (e) {
        setStatus('error');
        setError(e instanceof Error ? e.message : '저장에 실패했습니다');
      }
    },
    [uid, entryId, date, isNew],
  );

  const scheduleSave = useCallback(
    (text: string) => {
      if (timerRef.current) clearTimeout(timerRef.current);
      timerRef.current = setTimeout(() => void persist(text), DEBOUNCE_MS);
    },
    [persist],
  );

  const onChange = useCallback(
    (text: string) => {
      setBody(text);
      setStatus('idle');
      scheduleSave(text);
    },
    [scheduleSave],
  );

  const flush = useCallback(() => {
    if (timerRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;
    }
    return persist(body);
  }, [persist, body]);

  useEffect(() => {
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, []);

  return { body, setBody: onChange, status, error, flush };
}
