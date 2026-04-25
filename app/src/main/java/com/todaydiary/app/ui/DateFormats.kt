package com.todaydiary.app.ui

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val koreanLocale = Locale.KOREAN

fun formatHeaderDate(date: LocalDate): String {
    val dow = date.dayOfWeek.getDisplayName(TextStyle.SHORT, koreanLocale)
    return "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth} ($dow)"
}

fun formatSubtitleDate(date: LocalDate): String {
    val dow = date.dayOfWeek.getDisplayName(TextStyle.SHORT, koreanLocale)
    return "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일 ($dow)"
}

fun formatListItemDate(date: LocalDate): String {
    val dow = date.dayOfWeek.getDisplayName(TextStyle.SHORT, koreanLocale)
    return "${date.monthValue}월 ${date.dayOfMonth}일 ($dow)"
}

