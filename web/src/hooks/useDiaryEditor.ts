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
  const [entryDate, setEntryDate] = useState(date);
  const [status, setStatus] = useState<AutosaveStatus>('idle');
  const [error, setError] = useState<string | null>(null);
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const photosRef = useRef(photos);
  photosRef.current = photos;

  const entryDateRef = useRef(entryDate);
  entryDateRef.current = entryDate;

  useEffect(() => {
    setBody(initialBody);
    setEntryDate(date);
    setStatus('idle');
    setError(null);
  }, [entryId, initialBody, date]);

  const persist = useCallback(
    async (text: string, saveDate?: string) => {
      const dateToSave = saveDate ?? entryDateRef.current;
      setStatus('saving');
      setError(null);
      const entry: DiaryEntry = {
        id: entryId,
        date: dateToSave,
        body: text,
        photos: photosRef.current,
      };

      try {
        if (text.trim() === '') {
          if (isNew) {
            await deleteDiaryEntry(uid, entryId, dateToSave);
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
    [uid, entryId, isNew],
  );

  const scheduleSave = useCallback(
    (text: string, saveDate?: string) => {
      if (timerRef.current) clearTimeout(timerRef.current);
      timerRef.current = setTimeout(
        () => void persist(text, saveDate),
        DEBOUNCE_MS,
      );
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

  const changeDate = useCallback(
    (newDate: string) => {
      if (!newDate) return;
      setEntryDate(newDate);
      setStatus('idle');
      if (body.trim() !== '') {
        scheduleSave(body, newDate);
      }
    },
    [body, scheduleSave],
  );

  const flush = useCallback(() => {
    if (timerRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;
    }
    return persist(body, entryDateRef.current);
  }, [persist, body]);

  useEffect(() => {
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, []);

  return {
    body,
    setBody: onChange,
    entryDate,
    setEntryDate: changeDate,
    status,
    error,
    flush,
  };
}
