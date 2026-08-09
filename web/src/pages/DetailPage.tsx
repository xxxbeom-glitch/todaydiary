import type { DiaryEntry } from '../features/diary';
import { Header } from '../components/layout/Header';
import { IconButton } from '../components/ui/IconButton';
import { formatDetailDate } from '../lib/date';

interface DetailPageProps {
  entry: DiaryEntry;
  onBack: () => void;
  onEdit: () => void;
  /** PC 분할 패널 — 닫기/more 없음, 더블클릭으로 수정 */
  embedded?: boolean;
}

export function DetailPage({
  entry,
  onBack,
  onEdit,
  embedded = false,
}: DetailPageProps) {
  return (
    <div className={embedded ? 'app-pane' : 'flex min-h-dvh flex-col'}>
      <Header
        left={
          embedded ? (
            <span className="w-10" aria-hidden="true" />
          ) : (
            <IconButton label="뒤로" onClick={onBack}>
              ←
            </IconButton>
          )
        }
        center={
          <span className="type-body-strong text-[14px]">{formatDetailDate(entry.date)}</span>
        }
        right={<span className="w-10" aria-hidden="true" />}
      />

      <article className="app-page app-page-prose flex-1">
        <div
          className="app-read-body app-read-body--editable"
          role="button"
          tabIndex={0}
          title="더블클릭하여 수정"
          aria-label="일기 본문, 더블클릭하면 수정"
          onDoubleClick={onEdit}
          onKeyDown={(e) => {
            if (e.key === 'Enter' || e.key === ' ') {
              e.preventDefault();
              onEdit();
            }
          }}
        >
          {entry.body.trim() || (
            <span style={{ color: 'var(--color-text-muted)' }}>내용이 없는 일기입니다.</span>
          )}
        </div>
      </article>
    </div>
  );
}
