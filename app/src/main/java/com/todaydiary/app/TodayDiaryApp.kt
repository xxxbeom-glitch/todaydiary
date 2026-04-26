package com.todaydiary.app

import android.app.Application
import com.todaydiary.app.data.FirestoreInstances

class TodayDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 자동 백업: [FirestoreInstances] lazy 초기화 시 (default)가 아닌 "diary" DB에 퍼시스턴스 설정
        FirestoreInstances.diary
    }
}

