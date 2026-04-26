package com.todaydiary.app

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class TodayDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // 자동 백업(동기화)의 1차 핵심:
        // - 오프라인에서도 write를 로컬에 저장
        // - 온라인이 되면 자동으로 서버에 동기화
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
    }
}

