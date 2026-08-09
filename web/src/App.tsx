import { useCallback, useEffect, useMemo, useState } from 'react';
import { useAuth } from './features/auth';
import {
  deleteDiaryEntry,
  newEntryId,
  useDiaries,
  type DiaryEntry,
} from './features/diary';
import { AppShell } from './components/layout/AppShell';
import { LoadingView } from './components/ui/LoadingView';
import { LoginPage } from './pages/LoginPage';
import { DiaryListPage } from './pages/DiaryListPage';
import { EditorPage } from './pages/EditorPage';
import { DetailPage } from './pages/DetailPage';
import { todayISO, yearMonthKey } from './lib/date';
import { useIsDesktopLayout } from './hooks/useMediaQuery';

type Screen = 'list' | 'editor' | 'detail';

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

  // PC: 우측은 항상 새 글 작성 준비 (목록만 보이는 상태 없음)
  useEffect(() => {
    if (!user || !isDesktop) return;
    if (screen === 'list') openCreate();
  }, [user, isDesktop, screen, openCreate]);

  const handleDelete = useCallback(async () => {
    if (!uid || !selected) return;
    try {
      await deleteDiaryEntry(uid, selected.id, selected.date);
      closePane();
    } catch (e) {
      setDiaryError(e instanceof Error ? e.message : '삭제에 실패했습니다.');
    }
  }, [uid, selected, setDiaryError, closePane]);

  const editorInitialBody = useMemo(() => {
    if (forceBlank) return '';
    return selected?.body ?? '';
  }, [forceBlank, selected]);

  const editorPhotos = selected?.photos ?? [];

  const liveSelected = useMemo(() => {
    if (!selected) return null;
    return entries.find((e) => e.id === selected.id) ?? selected;
  }, [entries, selected]);

  if (authLoading) {
    return (
      <AppShell className={isDesktop ? 'app-shell--fill' : undefined}>
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
          onBack={closePane}
        />
      )}

      {screen === 'detail' && liveSelected && (
        <DetailPage
          entry={liveSelected}
          embedded={isDesktop}
          onBack={() => closePane()}
          onEdit={() => openEdit(liveSelected)}
          onDelete={() => void handleDelete()}
        />
      )}

      {screen === 'editor' && (!uid || !activeId) && (
        <p className="app-page type-caption text-center">작성 정보를 불러오지 못했습니다.</p>
      )}
    </>
  );

  return (
    <AppShell className={isDesktop ? 'app-shell--fill' : undefined}>
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
        <div className="app-desktop-split">
          <div className="app-desktop-split__list">{list}</div>
          <div className="app-desktop-split__main">{mainPane}</div>
        </div>
      ) : (
        <>
          {screen === 'list' && list}
          {mainPane}
        </>
      )}
    </AppShell>
  );
}
