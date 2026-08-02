import fs from 'node:fs';
import path from 'node:path';

const src = path.resolve('src');
const wrongOpen = '<' + String.fromCharCode(109, 111, 116, 105, 111, 110);
const wrongClose = '</' + String.fromCharCode(109, 111, 116, 105, 111, 110) + '>';
const rightOpen = '<' + String.fromCharCode(100, 105, 118);
const rightClose = '</' + String.fromCharCode(100, 105, 118) + '>';

function fix(content) {
  return content.split(wrongOpen).join(rightOpen).split(wrongClose).join(rightClose);
}

const loadingView = `export function LoadingView({ label = '\\uBD88\\uB7EC\\uc624\\ub294 \\uc911\\u2026' }: { label?: string }) {
  return (
    ${wrongOpen}
      className="flex min-h-[40vh] flex-col items-center justify-center gap-3 text-neutral-600"
    >
      <span className="h-5 w-5 animate-spin rounded-full border-2 border-stone-300 border-t-stone-700" />
      <p className="text-sm font-medium text-neutral-800">{label}</p>
    ${wrongClose}
  );
}
`;

const errorBoundary = `import { Component, type ErrorInfo, type ReactNode } from 'react';

interface Props { children: ReactNode; }
interface State { error: Error | null; }

export class ErrorBoundary extends Component<Props, State> {
  state: State = { error: null };

  static getDerivedStateFromError(error: Error): State {
    return { error };
  }

  componentDidCatch(error: Error, info: ErrorInfo): void {
    console.error('[ErrorBoundary]', error, info.componentStack);
  }

  render() {
    if (this.state.error) {
      return (
        ${wrongOpen} className="mx-auto max-w-lg p-6 text-neutral-800" style={{ background: '#faf8f5' }}${wrongClose}
          <h1 className="text-lg font-semibold text-red-800">\\ud654\\uba74\\uc744 \\ubd88\\ub7ec\\uc624\\uc9c0 \\ubabb\\ud588\\uc2b5\\ub2c8\\ub2e4</h1>
          <p className="mt-2 text-sm text-neutral-600">{this.state.error.message}</p>
          <button
            type="button"
            className="mt-4 rounded-lg border border-neutral-300 bg-white px-4 py-2 text-sm text-neutral-900"
            onClick={() => this.setState({ error: null })}
          >
            \\ub2e4\\uc2dc \\uc2dc\\ub3c4
          </button>
        ${wrongClose}
      );
    }
    return this.props.children;
  }
}
`;

const loginPage = `import { debugBorder } from '../lib/debugUi';

interface LoginPageProps {
  onLogin: () => void;
  loading?: boolean;
  error?: string | null;
}

export function LoginPage({ onLogin, loading, error }: LoginPageProps) {
  return (
    ${wrongOpen} className={\`flex min-h-dvh flex-col items-center justify-center bg-stone-50 px-8 text-neutral-800 \${debugBorder()}\`}${wrongClose}
      <p className="text-sm font-medium text-red-600">[UI \\ud14c\\uc2a4\\ud2b8] \\ub85c\\uadf8\\uc778 \\ud654\\uba74</p>
      ${wrongOpen} className={\`mt-6 w-full max-w-sm text-center \${debugBorder()}\`}${wrongClose}
        <p className="text-xs tracking-widest text-neutral-500 uppercase">Today Diary</p>
        <h1 className="mt-4 text-4xl font-semibold text-neutral-900">\\ud558\\ub8e8\\uae30\\ub85d</h1>
        <p className="mt-6 text-sm leading-relaxed text-neutral-600">
          \\uc870\\uc6a9\\ud788 \\ud558\\ub8e8\\ub97c \\ub0a8\\uae30\\ub294 \\uacf3.
          <br />
          Android \\uc571\\uacfc \\uac19\\uc740 \\uacc4\\uc815\\uc73c\\ub85c \\uc774\\uc5b4\\uc9d1\\ub2c8\\ub2e4.
        </p>
        {error && (
          <p className="mt-6 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">
            {error}
          </p>
        )}
        <button
          type="button"
          disabled={loading}
          onClick={onLogin}
          className="mt-10 w-full rounded-xl border border-stone-300 bg-white py-3.5 text-[15px] font-medium text-neutral-900 shadow-sm hover:bg-stone-100 disabled:opacity-50"
        >
          {loading ? '\\uc5f0\\uacb0 \\uc911\\u2026' : 'Google\\ub85c \\uacc4\\uc18d\\ud558\\uae30'}
        </button>
      ${wrongClose}
    ${wrongClose}
  );
}
`;

const diaryList = `import { useMemo, useState } from 'react';
import type { DiaryEntry } from '../features/diary';
import { DiaryCard } from '../components/diary/DiaryCard';
import { FloatingWriteButton } from '../components/diary/FloatingWriteButton';
import { MonthPickerModal } from '../components/diary/MonthPickerModal';
import { EmptyState } from '../components/ui/EmptyState';
import { LoadingView } from '../components/ui/LoadingView';
import { Header } from '../components/layout/Header';
import { IconButton } from '../components/ui/IconButton';
import { debugBorder } from '../lib/debugUi';
import { collectMonthKeys, entryInMonth, formatMonthTitle, shiftMonthKey } from '../lib/date';

interface DiaryListPageProps {
  entries: DiaryEntry[];
  loading: boolean;
  monthKey: string;
  onMonthChange: (key: string) => void;
  onSelect: (entry: DiaryEntry) => void;
  onCreate: () => void;
  onLogout: () => void;
}

export function DiaryListPage({ entries, loading, monthKey, onMonthChange, onSelect, onCreate, onLogout }: DiaryListPageProps) {
  const [pickerOpen, setPickerOpen] = useState(false);
  const monthEntries = useMemo(() => entries.filter((e) => entryInMonth(e.date, monthKey)).sort((a, b) => b.date.localeCompare(a.date) || b.id.localeCompare(a.id)), [entries, monthKey]);
  const monthOptions = useMemo(() => collectMonthKeys(entries.map((e) => e.date), monthKey), [entries, monthKey]);

  return (
    ${wrongOpen} className={\`relative min-h-dvh pb-24 text-neutral-800 \${debugBorder()}\`}${wrongClose}
      <Header
        left={<button type="button" onClick={onLogout} className="ml-1 rounded-lg px-2 py-1 text-xs text-neutral-600 hover:bg-stone-200">\\ub098\\uac00\\uae30</button>}
        center={<button type="button" onClick={() => setPickerOpen(true)} className="inline-flex items-center gap-1 text-[16px] font-medium text-neutral-900">{formatMonthTitle(monthKey)}<span className="text-xs text-neutral-500">\\u25be</span></button>}
        right={<${wrongOpen.replace('<', '')} className="flex gap-0.5">${wrongClose}<IconButton label="\\uc774\\uc804 \\ub2ec" onClick={() => onMonthChange(shiftMonthKey(monthKey, -1))}>\\u2039</IconButton><IconButton label="\\ub2e4\\uc74c \\ub2ec" onClick={() => onMonthChange(shiftMonthKey(monthKey, 1))}>\\u203a</IconButton></${wrongClose.replace('</', '').replace('>', '')}>}
      />
      <main className={\`px-4 py-6 md:px-6 \${debugBorder()}\`}>
        {loading ? <LoadingView label="\\uc77c\\uae30\\ub97c \\ubd88\\ub7ec\\uc624\\ub294 \\uc911\\u2026" /> : monthEntries.length === 0 ? <EmptyState title="\\uc774 \\ub2ec\\uc5d0\\ub294 \\uc544\\uc9c1 \\uae00\\uc774 \\uc5c6\\uc5b4\\uc694" description="\\uc624\\ub978\\ucabd \\uc544\\ub798 \\ubc84\\ud2bc\\uc73c\\ub85c \\uc624\\ub298\\uc758 \\ud55c \\ud398\\uc774\\uc9c0\\ub97c \\ucc44\\uc6cc \\ubcf4\\uc138\\uc694." /> : (
          <ul className="flex flex-col gap-3">{monthEntries.map((entry) => <li key={entry.id || entry.date}><DiaryCard entry={entry} onClick={() => onSelect(entry)} /></li>)}</ul>
        )}
      </main>
      <FloatingWriteButton onClick={onCreate} />
      {pickerOpen && <MonthPickerModal value={monthKey} months={monthOptions} onChange={onMonthChange} onClose={() => setPickerOpen(false)} />}
    ${wrongClose}
  );
}
`;

// diaryList has broken right= - simplify diary list write separately

const writes = {
  'components/ui/LoadingView.tsx': loadingView,
  'components/ErrorBoundary.tsx': errorBoundary,
  'pages/LoginPage.tsx': loginPage,
};

for (const [rel, raw] of Object.entries(writes)) {
  const text = fix(raw.replace(/\\u([0-9a-fA-F]{4})/g, (_, h) => String.fromCharCode(parseInt(h, 16))));
  fs.writeFileSync(path.join(src, rel), text, 'utf8');
  console.log('fixed', rel);
}

// Fix all existing tsx with wrong tags
for (const file of fs.readdirSync(src, { recursive: true })) {
  if (!file.endsWith('.tsx')) continue;
  const fp = path.join(src, file);
  let c = fs.readFileSync(fp, 'utf8');
  if (c.includes(wrongOpen)) {
    fs.writeFileSync(fp, fix(c), 'utf8');
    console.log('patched', file);
  }
}
