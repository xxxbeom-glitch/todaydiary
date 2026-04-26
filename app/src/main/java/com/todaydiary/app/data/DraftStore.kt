package com.todaydiary.app.data

import android.content.Context
import java.time.LocalDate

class DraftStore(
    private val context: Context,
) {
    private val prefs by lazy { context.getSharedPreferences("diary_drafts", Context.MODE_PRIVATE) }

    fun loadDraft(date: LocalDate): String {
        return prefs.getString(key(date), "") ?: ""
    }

    fun saveDraft(date: LocalDate, body: String) {
        prefs.edit().putString(key(date), body).apply()
    }

    fun clearDraft(date: LocalDate) {
        prefs.edit().remove(key(date)).apply()
    }

    private fun key(date: LocalDate) = "draft_${date}"
}

