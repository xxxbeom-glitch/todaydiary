package com.todaydiary.app.data

import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate

/**
 * 불러오기 2회 / 저장 차이로 생기는 **미세한 공백·개행** 차이를 없앤 뒤 같은 일기인지 본다.
 */
object DiaryBodyDedupe {
    fun normForDedupe(body: String): String =
        body
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .replace(Regex("""\s+"""), " ")
            .trim()

    private fun key(date: LocalDate, body: String) = date to normForDedupe(body)

    /** 같은 날 + [normForDedupe]로 같으면 1건만(사진·id 우선) */
    fun pickOnePerDuplicateKey(entries: List<DiaryEntry>): List<DiaryEntry> {
        if (entries.size < 2) return entries
        return entries
            .groupBy { key(it.date, it.body) }
            .map { (_, group) ->
                if (group.size == 1) {
                    group.first()
                } else {
                    group.maxWith(
                        compareByDescending<DiaryEntry> { it.photos.size }
                            .thenBy { it.id }
                    )
                }
            }
    }
}
