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

    fun loadDraftForEntry(entryId: String, date: LocalDate): String {
        if (entryId.isNotBlank()) {
            val v = prefs.getString(keyEntryBody(entryId), null)
            if (v != null) return v
        }
        // legacy: 날짜 키
        return loadDraft(date)
    }

    fun saveDraft(entryId: String, date: LocalDate, body: String) {
        if (entryId.isBlank()) {
            // legacy path
            saveDraft(date, body)
            return
        }
        if (body.isBlank()) {
            clearDraft(entryId, date)
            return
        }
        prefs.edit()
            .putString(keyEntryDate(entryId), date.toString())
            .putString(keyEntryBody(entryId), body)
            .apply()
    }

    fun saveDraft(date: LocalDate, body: String) {
        if (body.isBlank()) {
            // 빈 문자열은 "작성 취소"에 가깝게 취급: 키를 남기지 않음
            clearDraft(date)
            return
        }
        prefs.edit().putString(key(date), body).apply()
    }

    fun clearDraft(date: LocalDate) {
        prefs.edit().remove(key(date)).apply()
    }

    fun clearDraft(entryId: String, date: LocalDate) {
        if (entryId.isBlank()) {
            clearDraft(date)
            return
        }
        prefs.edit()
            .remove(keyEntryDate(entryId))
            .remove(keyEntryBody(entryId))
            .apply()
    }

    fun listDraftDates(): List<LocalDate> {
        return prefs.all.keys.mapNotNull { k ->
            if (!k.startsWith("draft_")) return@mapNotNull null
            val raw = k.removePrefix("draft_")
            runCatching { LocalDate.parse(raw) }.getOrNull()
        }.sortedDescending()
    }

    fun listDraftEntries(): List<com.todaydiary.app.ui.models.DiaryEntry> {
        val modern = prefs.all.keys
            .mapNotNull { k ->
                if (!k.startsWith("draft_e_date_")) return@mapNotNull null
                val id = k.removePrefix("draft_e_date_")
                if (id.isBlank()) return@mapNotNull null
                val dateStr = prefs.getString(k, null) ?: return@mapNotNull null
                val body = prefs.getString("draft_e_body_$id", "") ?: ""
                val date = runCatching { LocalDate.parse(dateStr) }.getOrNull() ?: return@mapNotNull null
                if (body.isBlank()) return@mapNotNull null
                com.todaydiary.app.ui.models.DiaryEntry(id = id, date = date, body = body)
            }

        val legacy = listDraftDates().mapNotNull { date ->
            val body = loadDraft(date)
            if (body.isBlank()) null else com.todaydiary.app.ui.models.DiaryEntry(date = date, body = body)
        }
        return (modern + legacy).sortedByDescending { it.date }
    }

    private fun key(date: LocalDate) = "draft_${date}"

    private fun keyEntryDate(id: String) = "draft_e_date_$id"
    private fun keyEntryBody(id: String) = "draft_e_body_$id"
}

