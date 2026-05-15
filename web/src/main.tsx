import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { logImportMetaEnvDev } from './lib/env';
import App from './App.tsx';
import { ErrorBoundary } from './components/ErrorBoundary.tsx';

logImportMetaEnvDev();

const rootEl = document.getElementById('root');
if (!rootEl) {
  document.body.innerHTML =
    '<p style="padding:24px;font-family:system-ui;color:#b00020">#root 요소를 찾을 수 없습니다.</p>';
} else {
  createRoot(rootEl).render(
    <StrictMode>
      <ErrorBoundary>
        <App />
      </ErrorBoundary>
    </StrictMode>,
  );
}
