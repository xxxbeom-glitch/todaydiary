import type { User } from 'firebase/auth';
import type { AppFont, AppTheme } from '../../lib/preferences';

interface SettingsPanelProps {
  user: User;
  theme: AppTheme;
  font: AppFont;
  onThemeChange: (theme: AppTheme) => void;
  onFontChange: (font: AppFont) => void;
  onLogout: () => void;
  onClose: () => void;
}

export function SettingsPanel({
  user,
  theme,
  font,
  onThemeChange,
  onFontChange,
  onLogout,
  onClose,
}: SettingsPanelProps) {
  const displayName = user.displayName?.trim() || user.email || '로그인됨';

  return (
    <div className="app-settings" role="presentation" onClick={onClose}>
      <aside
        className="app-settings__panel"
        role="dialog"
        aria-modal="true"
        aria-labelledby="app-settings-title"
        onClick={(e) => e.stopPropagation()}
      >
        <header className="app-settings__header">
          <h2 id="app-settings-title" className="type-section-title">
            설정
          </h2>
          <button type="button" className="app-icon-btn" aria-label="닫기" onClick={onClose}>
            ✕
          </button>
        </header>

        <section className="app-settings__section">
          <h3 className="app-settings__label">계정</h3>
          <p className="app-settings__value type-body">{displayName}</p>
          {user.email && displayName !== user.email && (
            <p className="type-caption mt-1">{user.email}</p>
          )}
          <button
            type="button"
            className="app-btn app-btn-ghost app-settings__action"
            onClick={onLogout}
          >
            로그아웃
          </button>
        </section>

        <section className="app-settings__section">
          <h3 className="app-settings__label">테마</h3>
          <div className="app-settings__segment" role="group" aria-label="테마">
            <button
              type="button"
              className={`app-settings__seg-btn${theme === 'light' ? ' is-active' : ''}`}
              onClick={() => onThemeChange('light')}
            >
              라이트
            </button>
            <button
              type="button"
              className={`app-settings__seg-btn${theme === 'dark' ? ' is-active' : ''}`}
              onClick={() => onThemeChange('dark')}
            >
              다크
            </button>
          </div>
        </section>

        <section className="app-settings__section">
          <h3 className="app-settings__label">폰트</h3>
          <div className="app-settings__segment" role="group" aria-label="폰트">
            <button
              type="button"
              className={`app-settings__seg-btn${font === 'pretendard' ? ' is-active' : ''}`}
              onClick={() => onFontChange('pretendard')}
            >
              Pretendard
            </button>
            <button
              type="button"
              className={`app-settings__seg-btn${font === 'system' ? ' is-active' : ''}`}
              onClick={() => onFontChange('system')}
            >
              시스템
            </button>
          </div>
        </section>
      </aside>
    </div>
  );
}
