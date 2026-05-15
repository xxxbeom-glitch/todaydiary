import { useCallback, useMemo, useState } from 'react';
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
import { debugBorder } from './lib/debugUi';

type Screen = 'list' | 'editor' | 'detail';

export default function App() {
  const { user, loading: authLoading, error: authError, setError: setAuthError, loginWithGoogle, logout } =
    useAuth();
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
    setScreen('detail');
  }, []);

  const handleDelete = useCallback(async () => {
    if (!uid || !selected) return;
    try {
      await deleteDiaryEntry(uid, selected.id, selected.date);
      setSelected(null);
      setScreen('list');
    } catch (e) {
      setDiaryError(e instanceof Error ? e.message : '??? ??????');
    }
  }, [uid, selected, setDiaryError]);

  const editorInitialBody = useMemo(() => {
    if (forceBlank) return '';
    return selected?.body ?? '';
  }, [forceBlank, selected]);

  const editorPhotos = selected?.photos ?? [];

  /* ?? ??? ??? ??? */
  const debugStrip = (
    <p
      className={`border-b border-red-300 bg-red-50 px-3 py-1.5 text-center text-xs font-medium text-red-800 ${debugBorder()}`}
    >
      UI ?? OK � auth={authLoading ? 'loading' : user ? `uid:${user.uid.slice(0, 8)}?` : 'guest'} � screen=
      {screen}
    </p>
  );

  if (authLoading) {
    return (
      <AppShell>
        {debugStrip}
        <LoadingView label="?? ?? ?? (loading)" />
      </AppShell>
    );
  }

  if (!user) {
    return (
      <>
        {debugStrip}
        <LoginPage
          onLogin={() => void handleLogin()}
          loading={loginPending}
          error={uiError}
        />
      </>
    );
  }

  return (
    <AppShell>
      {debugStrip}

      {uiError && screen === 'list' && (
        <p className="border-b border-red-200 bg-red-50 px-4 py-2 text-center text-xs text-red-800">
          {uiError}
          <button
            type="button"
            className="ml-2 underline"
            onClick={() => {
              setAuthError(null);
              setDiaryError(null);
            }}
          >
            ??
          </button>
        </p>
      )}

      {screen === 'list' && (
        <DiaryListPage
          entries={entries}
          loading={!ready}
          monthKey={monthKey}
          onMonthChange={setMonthKey}
          onSelect={openView}
          onCreate={openCreate}
          onLogout={() => void logout()}
        />
      )}

      {screen === 'editor' && uid && activeId && (
        <EditorPage
          uid={uid}
          entryId={activeId}
          date={editorDate}
          initialBody={editorInitialBody}
          isNew={forceBlank}
          photos={editorPhotos}
          onBack={() => {
            setForceBlank(false);
            setScreen('list');
          }}
        />
      )}

      {screen === 'detail' && selected && (
        <DetailPage
          entry={selected}
          onBack={() => setScreen('list')}
          onEdit={() => openEdit(selected)}
          onDelete={() => void handleDelete()}
        />
      )}

      {/* screen ?? ??? ? ? ?? ?? */}
      {screen === 'editor' && (!uid || !activeId) && (
        <p className="p-6 text-center text-neutral-700">???? ? ? ????. ???? ??? ???.</p>
      )}
    </AppShell>
  );
}
