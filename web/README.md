# 하루기록 Web

Android 앱과 동일 Firebase 프로젝트·Firestore **`diary`** DB를 사용합니다.

## 빠른 시작 (3단계)

```bash
cd web
npm install
cp .env.example .env.local
# .env.local 에 Firebase 웹 앱 설정 입력 (키는 커밋하지 않음)
npm run dev
```

**`.env.local`이 필요합니다.** Vite가 로컬 개발 시 이 파일만 읽으며, Git에는 포함되지 않습니다.  
자세한 절차: [docs/setup.md](docs/setup.md)

## 문서

- [3단계 Firebase 연동](docs/setup.md)
- [Firestore 스키마](docs/firestore-schema.md)
- [Android 상세 분석](docs/android-data-structure.md)

## 소스 구조

```txt
web/src/
├── lib/
│   ├── firebase.ts           # App, Auth, named Firestore `diary`
│   └── firestore/            # 경로·필드 상수
└── features/
    ├── auth/                 # Google Auth, users/{uid}
    └── diary/                # users/{uid}/diaries
```

`App.tsx`는 로그인·일기 건수만 보여 주는 **최소 개발 셸**입니다.

## Vercel 배포

- 저장소 루트 [`vercel.json`](../vercel.json) → `web/dist` 배포 (Android 루트 404 방지)
- Vercel 환경 변수에 `VITE_*` 전부 등록 (`.env.local`과 동일)
- 자세한 내용: [docs/setup.md](docs/setup.md#vercel-배포)

## 원칙

- Android `app/` 코드는 수정하지 않음
- Firebase 키는 `.env.local`에만 보관 (하드코딩 없음)
- 1차 범위: 텍스트 일기 (사진 UI 제외)
