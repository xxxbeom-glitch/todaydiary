package com.todaydiary.app.ui.models

import java.time.LocalDate

data class DiaryEntry(
    // Firestore 문서 ID(자동 ID). (레거시: 날짜를 문서ID로 쓰던 케이스는 빈 값일 수 있어 doc.id로 보강)
    val id: String = "",
    val date: LocalDate,
    val body: String,
    // Firestore/임포트: 에셋 상대 경로(예: Story_008/Images/파일). UI는 미표시, 편집 저장 시 photos 유지는 MainActivity에서 처리.
    val photos: List<String> = emptyList(),
)

