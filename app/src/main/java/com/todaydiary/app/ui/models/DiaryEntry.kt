package com.todaydiary.app.ui.models

import java.time.LocalDate

data class DiaryEntry(
    // Firestore 문서 ID(자동 ID). (레거시: 날짜를 문서ID로 쓰던 케이스는 빈 값일 수 있어 doc.id로 보강)
    val id: String = "",
    val date: LocalDate,
    val body: String,
    val photos: List<String> = emptyList(), // asset paths under `photos/`
)

