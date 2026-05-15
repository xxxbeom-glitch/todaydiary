import { useDiaryEditor } from '../hooks/useDiaryEditor';
import { AutosaveIndicator } from '../components/diary/AutosaveIndicator';
import { Header } from '../components/layout/Header';
import { IconButton } from '../components/ui/IconButton';
import { formatDetailDate } from '../lib/date';

interface EditorPageProps {
  uid: string;
  entryId: string;
  date: string;
  initialBody: string;
  isNew: boolean;
  photos: string[];
  onBack: (savedDate?: string) => void;
}

export function EditorPage({
  uid,
  entryId,
  date,
  initialBody,
  isNew,
  photos,
  onBack,
}: EditorPageProps) {
  const { body, setBody, entryDate, setEntryDate, status, error, flush } =
    useDiaryEditor({
      uid,
      entryId,
      date,
      initialBody,
      isNew,
      photos,
    });

  const handleBack = () => {
    void flush().finally(() => onBack(entryDate));
  };

  const todayMax = new Date().toISOString().slice(0, 10);

  return (
    <div className="flex min-h-dvh flex-col">
      <Header
        left={
          <IconButton label="뒤로" onClick={handleBack}>
            ←
          </IconButton>
        }
        center={
          <div className="flex flex-col items-center gap-0.5">
            <span className="type-caption">{isNew ? '새 글' : '수정'}</span>
            <span className="type-body-strong text-[14px]">{formatDetailDate(entryDate)}</span>
          </div>
        }
        right={<AutosaveIndicator status={status} />}
      />

      {isNew && (
        <div
          className="border-b border-[var(--color-border)] px-[18px] py-4"
          style={{ backgroundColor: 'var(--color-surface)' }}
        >
          <label htmlFor="diary-date" className="type-caption mb-2 block uppercase tracking-wide">
            작성일
          </label>
          <input
            id="diary-date"
            type="date"
            value={entryDate}
            max={todayMax}
            onChange={(e) => setEntryDate(e.target.value)}
            className="app-input"
          />
          <p className="type-caption mt-2">선택한 날짜로 저장됩니다.</p>
        </div>
      )}

      {error && <p className="app-banner">{error}</p>}

      <div className="app-page flex-1">
        <textarea
          value={body}
          onChange={(e) => setBody(e.target.value)}
          placeholder="오늘 무엇이 마음에 남았나요…"
          autoFocus={!isNew}
          className="app-textarea"
        />
      </div>
    </div>
  );
}
