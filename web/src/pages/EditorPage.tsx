import { useEffect, useRef } from 'react';
import { useDiaryEditor } from '../hooks/useDiaryEditor';
import { DatePickerField } from '../components/diary/DatePickerField';
import { IconButton } from '../components/ui/IconButton';
import { formatEntryDateLabel, todayISO } from '../lib/date';

interface EditorPageProps {
  uid: string;
  entryId: string;
  date: string;
  initialBody: string;
  isNew: boolean;
  photos: string[];
  onBack: (savedDate?: string) => void;
  embedded?: boolean;
  onRegisterFlush?: (flush: (() => Promise<void>) | null) => void;
}

export function EditorPage({
  uid,
  entryId,
  date,
  initialBody,
  isNew,
  photos,
  onBack,
  embedded = false,
  onRegisterFlush,
}: EditorPageProps) {
  const { body, setBody, entryDate, setEntryDate, error, flush } = useDiaryEditor({
    uid,
    entryId,
    date,
    initialBody,
    isNew,
    photos,
  });

  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (!onRegisterFlush) return;
    onRegisterFlush(() => flush());
    return () => onRegisterFlush(null);
  }, [onRegisterFlush, flush]);

  const finish = () => {
    void flush().finally(() => onBack(entryDate));
  };

  /** 텍스트 영역 밖을 클릭하면 즉시 저장 (수정 중이면 뷰로 복귀) */
  const handlePanePointerDown = (e: React.PointerEvent<HTMLElement>) => {
    const target = e.target as HTMLElement;
    if (textareaRef.current?.contains(target)) return;
    if (target.closest('[data-editor-chrome]')) return;

    void flush().finally(() => {
      if (!isNew) onBack(entryDate);
    });
  };

  return (
    <div
      className={embedded ? 'app-pane' : 'flex min-h-dvh flex-col'}
      onPointerDown={handlePanePointerDown}
    >
      <div className="app-page app-page-prose app-prose-fill">
        {!embedded && (
          <div className="mb-2" data-editor-chrome>
            <IconButton label="뒤로" onClick={finish}>
              ←
            </IconButton>
          </div>
        )}

        <div className="app-entry-date-block" data-editor-chrome>
          {isNew ? (
            <DatePickerField
              value={entryDate}
              max={todayISO()}
              onChange={setEntryDate}
            />
          ) : (
            <p className="app-entry-date">{formatEntryDateLabel(entryDate)}</p>
          )}
        </div>

        {error && (
          <p className="app-banner mb-3" data-editor-chrome>
            {error}
          </p>
        )}

        <textarea
          ref={textareaRef}
          value={body}
          onChange={(e) => setBody(e.target.value)}
          placeholder="오늘 무엇이 마음에 남았나요…"
          autoFocus={embedded || !isNew}
          className="app-textarea"
        />
      </div>
    </div>
  );
}
