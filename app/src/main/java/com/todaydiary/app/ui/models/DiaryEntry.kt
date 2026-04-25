package com.todaydiary.app.ui.models

import java.time.LocalDate

data class DiaryEntry(
    val date: LocalDate,
    val body: String,
    val photos: List<String> = emptyList(), // asset paths under `photos/`
)

