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
  onBack: () => void;
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
  const { body, setBody, status, error, flush } = useDiaryEditor({
    uid,
    entryId,
    date,
    initialBody,
    isNew,
    photos,
  });

  const handleBack = () => {
    void flush().finally(onBack);
  };

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
            <span className="text-xs text-ink-muted">{isNew ? '새 글' : '수정'}</span>
            <span className="font-serif text-[14px]">{formatDetailDate(date)}</span>
          </div>
        }
        right={<AutosaveIndicator status={status} />}
      />

      {error && (
        <p className="border-b border-red-100 bg-red-50/60 px-4 py-2 text-center text-xs text-red-800">
          {error}
        </p>
      )}

      <div className="mx-auto w-full max-w-prose flex-1 px-5 py-6 md:px-8">
        <textarea
          value={body}
          onChange={(e) => setBody(e.target.value)}
          placeholder="오늘 무엇이 마음에 남았나요…"
          autoFocus
          className="min-h-[calc(100dvh-8rem)] w-full resize-none border-0 bg-transparent font-serif text-[17px] leading-[1.85] text-ink/90 outline-none placeholder:text-ink-muted/50"
        />
      </div>
    </div>
  );
}
