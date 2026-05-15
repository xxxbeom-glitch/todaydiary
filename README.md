# 하루기록 (Today Diary)

일기 앱 — Android 네이티브 + 웹 클라이언트.

| 경로 | 설명 |
|------|------|
| [`app/`](app/) | Android (Kotlin, Jetpack Compose, Firebase) |
| [`web/`](web/) | Web (React, Vite, Firebase) — Android와 Firestore 동기화 |

## 웹앱 빠른 시작

```bash
cd web
cp .env.example .env
# .env에 Firebase 웹 설정 입력
npm install
npm run dev
```

자세한 내용은 [web/README.md](web/README.md)와 [web/docs/android-data-structure.md](web/docs/android-data-structure.md)를 참고하세요.

## 원칙

- Android 앱 코드는 `app/`에서만 수정
- 웹 작업은 `web/`에서만 진행
- Firestore 스키마는 Android 앱 구현을 기준으로 유지
