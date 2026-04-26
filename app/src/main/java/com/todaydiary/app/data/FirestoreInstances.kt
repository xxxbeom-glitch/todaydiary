package com.todaydiary.app.data

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object FirestoreInstances {
    /**
     * Firebase 콘솔 Firestore → 데이터베이스에서 만든 **이름**과 동일해야 함.
     * (default) 는 다른 앱이 쓰는 경우 named DB에 분리.
     */
    const val DIARY_DATABASE_ID = "diary"

    val diary: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(FirebaseApp.getInstance(), DIARY_DATABASE_ID).apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        }
    }
}
