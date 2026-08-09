import type { User } from 'firebase/auth';
import {
  FONT_OPTIONS,
  FONT_SIZE_OPTIONS,
  type AppFont,
  type AppFontSize,
  type AppTheme,
} from '../../lib/preferences';

interface SettingsPanelProps {
  user: User;
  theme: AppTheme;
  font: AppFont;
  fontSize: AppFontSize;
  onThemeChange: (theme: AppTheme) => void;
  onFontChange: (font: AppFont) => void;
  onFontSizeChange: (size: AppFontSize) => void;
  onLogout: () => void;
  onClose: () => void;
}

export function SettingsPanel({
  user,
  theme,
  font,
  fontSize,
  onThemeChange,
  onFontChange,
  onFontSizeChange,
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
          <h3 className="app-settings__label">글자 크기</h3>
          <div className="app-settings__segment app-settings__segment--3" role="group" aria-label="글자 크기">
            {FONT_SIZE_OPTIONS.map((option) => (
              <button
                key={option.id}
                type="button"
                className={`app-settings__seg-btn${fontSize === option.id ? ' is-active' : ''}`}
                onClick={() => onFontSizeChange(option.id)}
              >
                {option.label}
              </button>
            ))}
          </div>
        </section>

        <section className="app-settings__section">
          <h3 className="app-settings__label">폰트</h3>
          <div className="app-settings__font-list" role="group" aria-label="폰트">
            {FONT_OPTIONS.map((option) => (
              <button
                key={option.id}
                type="button"
                className={`app-settings__font-btn app-settings__font-btn--${option.id}${font === option.id ? ' is-active' : ''}`}
                onClick={() => onFontChange(option.id)}
              >
                <span className="app-settings__font-name">{option.label}</span>
                <span className="app-settings__font-sample">{option.sample}</span>
              </button>
            ))}
          </div>
        </section>
      </aside>
    </div>
  );
}
