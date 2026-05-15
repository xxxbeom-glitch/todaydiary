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
          <span className="type-body-strong text-[14px]">{formatDetailDate(entry.date)}</span>
        }
        right={
          <IconButton label="더보기" onClick={() => setMenuOpen((v) => !v)}>
            ⋯
          </IconButton>
        }
      />

      <article className="app-page flex-1">
        <div className="app-read-body">
          {entry.body.trim() || (
            <span style={{ color: 'var(--color-text-muted)' }}>내용이 없는 일기입니다.</span>
          )}
        </div>
      </article>

      {menuOpen && (
        <>
          <button
            type="button"
            className="fixed inset-0 z-40 bg-[#2b2721]/15"
            aria-label="메뉴 닫기"
            onClick={() => {
              setMenuOpen(false);
              setConfirmDelete(false);
            }}
          />
          <div
            className="fixed right-[max(18px,calc(50%-215px))] top-14 z-50 w-44 overflow-hidden border border-[var(--color-border)]"
            style={{
              borderRadius: 'var(--radius-card)',
              backgroundColor: 'var(--color-surface)',
              boxShadow: 'var(--shadow-card)',
            }}
          >
            <button
              type="button"
              className="type-body block w-full px-4 py-3.5 text-left active:bg-[var(--color-surface-muted)]"
              onClick={() => {
                setMenuOpen(false);
                onEdit();
              }}
            >
              수정
            </button>
            <button
              type="button"
              className="type-body block w-full border-t border-[var(--color-border)] px-4 py-3.5 text-left active:bg-[var(--color-danger-soft)]"
              style={{ color: 'var(--color-danger)' }}
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
