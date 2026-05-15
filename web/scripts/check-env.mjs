/**
 * web/.env.local 이 Vite 에서 읽히는지 확인 (값은 출력하지 않음)
 * 사용: cd web && npm run check:env
 */
import { loadEnv } from 'vite';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const rootDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const env = loadEnv('development', rootDir, '');

const required = [
  'VITE_FIREBASE_API_KEY',
  'VITE_FIREBASE_AUTH_DOMAIN',
  'VITE_FIREBASE_PROJECT_ID',
  'VITE_FIREBASE_APP_ID',
];

const missing = required.filter((k) => !env[k]?.trim());
if (missing.length) {
  console.error('MISSING in', path.join(rootDir, '.env.local'), ':', missing.join(', '));
  process.exit(1);
}

console.log('OK —', required.length, 'required keys set in', path.join(rootDir, '.env.local'));
