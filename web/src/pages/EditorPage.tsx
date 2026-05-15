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
            <span className="text-xs text-neutral-500">{isNew ? '새 글' : '수정'}</span>
            <span className="text-[14px]">{formatDetailDate(entryDate)}</span>
          </div>
        }
        right={<AutosaveIndicator status={status} />}
      />

      {isNew && (
        <div className="border-b border-stone-200/60 bg-white/60 px-5 py-3">
          <label
            htmlFor="diary-date"
            className="mb-1.5 block text-[11px] tracking-wide text-neutral-500 uppercase"
          >
            작성일
          </label>
          <input
            id="diary-date"
            type="date"
            value={entryDate}
            max={todayMax}
            onChange={(e) => setEntryDate(e.target.value)}
            className="w-full rounded-xl border border-stone-200 bg-white px-3 py-2.5 text-[14px] text-neutral-800 outline-none focus:border-stone-400"
          />
          <p className="mt-1.5 text-[11px] text-neutral-400">
            선택한 날짜로 저장됩니다. 오늘 이전 날짜도 선택할 수 있어요.
          </p>
        </div>
      )}

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
          autoFocus={!isNew}
          className="min-h-[calc(100dvh-12rem)] w-full resize-none border-0 bg-transparent text-[17px] leading-[1.85] text-neutral-900/90 outline-none placeholder:text-neutral-500/50"
        />
      </div>
    </div>
  );
}
