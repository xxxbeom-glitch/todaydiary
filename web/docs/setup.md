# 3단계: Firebase 웹 연동

Android 앱(`app/`)과 **같은 Firebase 프로젝트**를 쓰며, Firestore는 **named database `diary`** 에 연결합니다.

## Android에서 확인한 구조

| 항목 | Android | Web |
|------|---------|-----|
| Firestore DB | `FirestoreInstances.DIARY_DATABASE_ID` = `"diary"` | `getFirestore(app, 'diary')` |
| Auth | `FirebaseAuth.getInstance()` | `getAuth()` |
| 일기 경로 | `users/{uid}/diaries/{docId}` | 동일 |
| 삭제 아카이브 | `users/{uid}/deletedDiaries/{docId}` | 동일 |
| 사용자 프로필 | `users/{uid}` | 동일 |

상세 필드: [firestore-schema.md](firestore-schema.md)

---

## 환경 변수 (필수)

실제 API 키는 **저장소에 커밋하지 않습니다.**

1. `web/.env.example` 을 복사합니다.
2. 로컬 전용 파일 **`web/.env.local`** 을 만듭니다.

```bash
cd web
cp .env.example .env.local
```

3. [Firebase Console](https://console.firebase.google.com/) → 프로젝트 `cole-c3f96` → **프로젝트 설정** → **내 앱** → **웹 앱** 추가(또는 기존 웹 앱) → 구성 값을 `.env.local`에 입력합니다.

| 변수 | 설명 |
|------|------|
| `VITE_FIREBASE_API_KEY` | Web API Key |
| `VITE_FIREBASE_AUTH_DOMAIN` | 보통 `{projectId}.firebaseapp.com` |
| `VITE_FIREBASE_PROJECT_ID` | `cole-c3f96` |
| `VITE_FIREBASE_STORAGE_BUCKET` | Storage bucket |
| `VITE_FIREBASE_MESSAGING_SENDER_ID` | 숫자 ID |
| `VITE_FIREBASE_APP_ID` | 웹 앱 App ID |
| `VITE_FIREBASE_DATABASE_ID` | **`diary` 고정** (Android와 동일) |

Vite는 `npm run dev` 시 `.env.local`을 자동 로드합니다.  
`.env.local`은 `.gitignore`에 포함되어 있어 Git에 올라가지 않습니다.

### Firebase Console 추가 설정

- **Authentication** → Sign-in method → **Google** 사용
- **Authentication** → Settings → **Authorized domains**에 `localhost` 포함
- **Firestore** → 데이터베이스 **`diary`** 가 생성되어 있는지 확인

---

## 코드 진입점

| 파일 | 역할 |
|------|------|
| `src/lib/firebase.ts` | App / Auth / named Firestore 초기화 |
| `src/features/auth/` | Google 로그인, `users/{uid}` 프로필 |
| `src/features/diary/` | `users/{uid}/diaries` listener·저장·삭제 |

---

## 실행

```bash
npm install
npm run dev
```

브라우저에서 Google 로그인 후 일기 건수가 표시되면 연동이 된 것입니다. UI는 이후 단계에서 구현합니다.
