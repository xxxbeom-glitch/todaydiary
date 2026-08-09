import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useAuth } from './features/auth';
import {
  deleteDiaryEntry,
  newEntryId,
  useDiaries,
  type DiaryEntry,
} from './features/diary';
import { AppShell } from './components/layout/AppShell';
import { DesktopTopBar } from './components/layout/DesktopTopBar';
import { ConfirmDialog } from './components/ui/ConfirmDialog';
import { LoadingView } from './components/ui/LoadingView';
import { LoginPage } from './pages/LoginPage';
import { DiaryListPage } from './pages/DiaryListPage';
import { EditorPage } from './pages/EditorPage';
import { DetailPage } from './pages/DetailPage';
import { todayISO, yearMonthKey, shiftMonthKey, collectMonthKeys } from './lib/date';
import { MonthPickerModal } from './components/diary/MonthPickerModal';
import { useIsDesktopLayout } from './hooks/useMediaQuery';

type Screen = 'list' | 'editor' | 'detail';
type FlushFn = () => Promise<void>;

export default function App() {
  const isDesktop = useIsDesktopLayout();
  const {
    user,
    loading: authLoading,
    error: authError,
    setError: setAuthError,
    loginWithGoogle,
  } = useAuth();
  const uid = user?.uid;
  const { entries, ready, error: diaryError, setError: setDiaryError } = useDiaries(uid);

  const [screen, setScreen] = useState<Screen>('list');
  const [monthKey, setMonthKey] = useState(() => yearMonthKey());
  const [selected, setSelected] = useState<DiaryEntry | null>(null);
  const [activeId, setActiveId] = useState('');
  const [editorDate, setEditorDate] = useState(todayISO);
  const [forceBlank, setForceBlank] = useState(false);
  const [loginPending, setLoginPending] = useState(false);
  const [monthPickerOpen, setMonthPickerOpen] = useState(false);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const flushRef = useRef<FlushFn | null>(null);

  const uiError = authError ?? diaryError;

  const handleLogin = useCallback(async () => {
    setLoginPending(true);
    setAuthError(null);
    try {
      await loginWithGoogle();
    } finally {
      setLoginPending(false);
    }
  }, [loginWithGoogle, setAuthError]);

  const openCreate = useCallback(() => {
    setSelected(null);
    setActiveId(newEntryId());
    setEditorDate(todayISO());
    setForceBlank(true);
    setScreen('editor');
  }, []);

  const openEdit = useCallback((entry: DiaryEntry) => {
    setSelected(entry);
    setActiveId(entry.id);
    setEditorDate(entry.date);
    setForceBlank(false);
    setScreen('editor');
  }, []);

  const openView = useCallback((entry: DiaryEntry) => {
    setSelected(entry);
    setActiveId(entry.id);
    setForceBlank(false);
    setScreen('detail');
  }, []);

  const closePane = useCallback(
    (savedDate?: string) => {
      if (savedDate) setMonthKey(savedDate.slice(0, 7));
      if (isDesktop) {
        openCreate();
        return;
      }
      setForceBlank(false);
      setSelected(null);
      setActiveId('');
      setScreen('list');
    },
    [isDesktop, openCreate],
  );

  useEffect(() => {
    if (!user || !isDesktop) return;
    if (screen === 'list') openCreate();
  }, [user, isDesktop, screen, openCreate]);

  const requestDelete = useCallback(() => {
    if (!uid) return;
    const target =
      screen === 'detail' && selected
        ? selected
        : screen === 'editor' && !forceBlank && selected
          ? selected
          : screen === 'editor' && !forceBlank && activeId
            ? { id: activeId, date: editorDate }
            : null;
    if (!target?.id) return;
    setDeleteConfirmOpen(true);
  }, [uid, screen, selected, forceBlank, activeId, editorDate]);

  const confirmDelete = useCallback(async () => {
    if (!uid) return;
    const target =
      screen === 'detail' && selected
        ? selected
        : screen === 'editor' && !forceBlank && selected
          ? selected
          : screen === 'editor' && !forceBlank && activeId
            ? { id: activeId, date: editorDate }
            : null;
    setDeleteConfirmOpen(false);
    if (!target?.id) return;
    try {
      await deleteDiaryEntry(uid, target.id, target.date);
      closePane();
    } catch (e) {
      setDiaryError(e instanceof Error ? e.message : '삭제에 실패했습니다.');
    }
  }, [uid, screen, selected, forceBlank, activeId, editorDate, closePane, setDiaryError]);

  const registerFlush = useCallback((fn: FlushFn | null) => {
    flushRef.current = fn;
  }, []);

  const editorInitialBody = useMemo(() => {
    if (forceBlank) return '';
    return selected?.body ?? '';
  }, [forceBlank, selected]);

  const editorPhotos = selected?.photos ?? [];

  const liveSelected = useMemo(() => {
    if (!selected) return null;
    return entries.find((e) => e.id === selected.id) ?? selected;
  }, [entries, selected]);

  const monthOptions = useMemo(
    () => collectMonthKeys(entries.map((e) => e.date), monthKey),
    [entries, monthKey],
  );

  const canGoNextMonth = monthOptions.includes(shiftMonthKey(monthKey, 1));

  const canDelete =
    (screen === 'detail' && Boolean(liveSelected)) ||
    (screen === 'editor' && !forceBlank && Boolean(activeId));

  if (authLoading) {
    return (
      <AppShell framed={isDesktop} className={isDesktop ? 'app-shell--fill' : undefined}>
        <LoadingView />
      </AppShell>
    );
  }

  if (!user) {
    return <LoginPage onLogin={() => void handleLogin()} loading={loginPending} error={uiError} />;
  }

  const list = (
    <DiaryListPage
      entries={entries}
      loading={!ready}
      monthKey={monthKey}
      onMonthChange={setMonthKey}
      onSelect={openView}
      onCreate={openCreate}
      selectedId={forceBlank || screen === 'list' ? undefined : activeId || selected?.id}
      embedded={isDesktop}
      hideMonthNav={isDesktop}
    />
  );

  const mainPane = (
    <>
      {screen === 'editor' && uid && activeId && (
        <EditorPage
          uid={uid}
          entryId={activeId}
          date={editorDate}
          initialBody={editorInitialBody}
          isNew={forceBlank}
          photos={editorPhotos}
          embedded={isDesktop}
          onRegisterFlush={registerFlush}
          onBack={(savedDate) => {
            if (savedDate) setMonthKey(savedDate.slice(0, 7));
            if (!forceBlank && selected) {
              setForceBlank(false);
              setScreen('detail');
              return;
            }
            closePane(savedDate);
          }}
        />
      )}

      {screen === 'detail' && liveSelected && (
        <DetailPage
          entry={liveSelected}
          embedded={isDesktop}
          onBack={() => closePane()}
          onEdit={() => openEdit(liveSelected)}
        />
      )}

      {screen === 'editor' && (!uid || !activeId) && (
        <p className="app-page type-caption text-center">작성 정보를 불러오지 못했습니다.</p>
      )}
    </>
  );

  return (
    <AppShell framed={isDesktop} className={isDesktop ? 'app-shell--fill' : undefined}>
      {uiError && (screen === 'list' || isDesktop) && (
        <div className="app-banner">
          {uiError}
          <button
            type="button"
            className="ml-2 underline"
            onClick={() => {
              setAuthError(null);
              setDiaryError(null);
            }}
          >
            닫기
          </button>
        </div>
      )}

      {isDesktop ? (
        <div className="app-desktop">
          <div className="app-desktop-split">
            <div className="app-desktop-split__list">{list}</div>
            <div className="app-desktop-split__main">
              <DesktopTopBar
                monthKey={monthKey}
                onPrevMonth={() => setMonthKey((k) => shiftMonthKey(k, -1))}
                onNextMonth={() => setMonthKey((k) => shiftMonthKey(k, 1))}
                onOpenMonthPicker={() => setMonthPickerOpen(true)}
                canGoNextMonth={canGoNextMonth}
                canDelete={canDelete}
                onDelete={requestDelete}
              />
              <div className="app-doc-frame">{mainPane}</div>
            </div>
          </div>
          {monthPickerOpen && (
            <MonthPickerModal
              value={monthKey}
              months={monthOptions}
              onChange={setMonthKey}
              onClose={() => setMonthPickerOpen(false)}
            />
          )}
          {deleteConfirmOpen && (
            <ConfirmDialog
              title="이 일기를 삭제할까요?"
              description="삭제하면 되돌릴 수 없습니다."
              confirmLabel="삭제"
              cancelLabel="취소"
              danger
              onConfirm={() => void confirmDelete()}
              onCancel={() => setDeleteConfirmOpen(false)}
            />
          )}
        </div>
      ) : (
        <>
          {screen === 'list' && list}
          {mainPane}
          {deleteConfirmOpen && (
            <ConfirmDialog
              title="이 일기를 삭제할까요?"
              description="삭제하면 되돌릴 수 없습니다."
              confirmLabel="삭제"
              cancelLabel="취소"
              danger
              onConfirm={() => void confirmDelete()}
              onCancel={() => setDeleteConfirmOpen(false)}
            />
          )}
        </>
      )}
    </AppShell>
  );
}
