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

  if (mode === 'development') {
    const viteKeys = Object.keys(env).filter((k) => k.startsWith('VITE_'));
    console.log('[vite] envDir=', rootDir);
    console.log('[vite] loaded VITE_* keys:', viteKeys);
    if (!env.VITE_FIREBASE_API_KEY) {
      console.warn(
        '[vite] VITE_FIREBASE_API_KEY 없음 — web/.env.local 확인, dev 서버는 web/ 에서 실행',
      );
    }
  }

  return {
    root: rootDir,
    envDir: rootDir,
    envPrefix: 'VITE_',
    plugins: [react(), tailwindcss()],
  };
});
