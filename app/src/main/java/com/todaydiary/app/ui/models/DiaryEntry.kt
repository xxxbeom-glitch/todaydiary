package com.todaydiary.app.ui.models

import java.time.LocalDate

data class DiaryEntry(
    val date: LocalDate,
    val body: String,
)

