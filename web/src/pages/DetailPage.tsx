import { useState } from 'react';
import type { DiaryEntry } from '../features/diary';
import { Header } from '../components/layout/Header';
import { IconButton } from '../components/ui/IconButton';
import { formatDetailDate } from '../lib/date';

interface DetailPageProps {
  entry: DiaryEntry;
  onBack: () => void;
  onEdit: () => void;
  onDelete: () => void;
}

export function DetailPage({ entry, onBack, onEdit, onDelete }: DetailPageProps) {
  const [menuOpen, setMenuOpen] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);

  const handleDelete = () => {
    if (!confirmDelete) {
      setConfirmDelete(true);
      return;
    }
    onDelete();
    setMenuOpen(false);
  };

  return (
    <div className="flex min-h-dvh flex-col">
      <Header
        left={
          <IconButton label="뒤로" onClick={onBack}>
            ←
          </IconButton>
        }
        center={
          <span className="font-serif text-[14px] text-ink/90">{formatDetailDate(entry.date)}</span>
        }
        right={
          <IconButton label="더보기" onClick={() => setMenuOpen((v) => !v)}>
            ⋯
          </IconButton>
        }
      />

      <article className="mx-auto w-full max-w-prose flex-1 px-5 py-8 md:px-8">
        <div
          className="min-h-[50vh] font-serif text-[17px] leading-[1.85] whitespace-pre-wrap text-ink/90"
          style={{
            backgroundImage:
              'repeating-linear-gradient(transparent, transparent 1.85em, var(--color-line-soft) 1.85em, var(--color-line-soft) calc(1.85em + 1px))',
            backgroundAttachment: 'local',
          }}
        >
          {entry.body.trim() || (
            <span className="text-ink-muted">내용이 없는 일기입니다.</span>
          )}
        </div>
      </article>

      {menuOpen && (
        <>
          <button
            type="button"
            className="fixed inset-0 z-40 bg-ink/10"
            aria-label="메뉴 닫기"
            onClick={() => {
              setMenuOpen(false);
              setConfirmDelete(false);
            }}
          />
          <div className="fixed right-4 top-16 z-50 w-44 overflow-hidden rounded-xl border border-line bg-paper shadow-sm">
            <button
              type="button"
              className="block w-full px-4 py-3 text-left text-sm hover:bg-paper-warm"
              onClick={() => {
                setMenuOpen(false);
                onEdit();
              }}
            >
              수정
            </button>
            <button
              type="button"
              className="block w-full border-t border-line px-4 py-3 text-left text-sm text-red-800/90 hover:bg-red-50/50"
              onClick={handleDelete}
            >
              {confirmDelete ? '정말 삭제할까요?' : '삭제'}
            </button>
          </div>
        </>
      )}
    </div>
  );
}
