import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

const rootDir = path.dirname(fileURLToPath(import.meta.url));

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // .env / .env.local 은 반드시 vite.config.ts 와 같은 폴더(web/) 기준
  const env = loadEnv(mode, rootDir, '');

  const required = [
    'VITE_FIREBASE_API_KEY',
    'VITE_FIREBASE_AUTH_DOMAIN',
    'VITE_FIREBASE_PROJECT_ID',
    'VITE_FIREBASE_APP_ID',
  ] as const;
  const missing = required.filter((k) => !env[k]?.trim());

  if (mode === 'development') {
    const viteKeys = Object.keys(env).filter((k) => k.startsWith('VITE_'));
    console.log('[vite] envDir=', rootDir);
    console.log('[vite] loaded VITE_* keys:', viteKeys);
    if (missing.length > 0) {
      console.warn('[vite] missing:', missing.join(', '), '— web/.env.local 확인');
    }
  }

  if (mode === 'production' && missing.length > 0) {
    throw new Error(
      `[vite] production 빌드에 Firebase env 가 없습니다: ${missing.join(', ')}\n` +
        '로컬: web/.env.local\n' +
        'Vercel: Settings → Environment Variables 에 VITE_* 등록 후 Redeploy\n' +
        '(Vite 는 빌드 시 env 를 번들에 박아 넣습니다. 런타임에 .env.local 을 읽지 않습니다.)',
    );
  }

  return {
    root: rootDir,
    envDir: rootDir,
    envPrefix: 'VITE_',
    plugins: [react(), tailwindcss()],
  };
});
