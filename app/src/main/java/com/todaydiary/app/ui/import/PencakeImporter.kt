package com.todaydiary.app.ui.import

import android.content.Context
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate

object PencakeImporter {
    private val dateRegex = Regex("""(\d{4})년\s*(\d{1,2})월\s*(\d{1,2})일""")
    private val articleNameRegex = Regex("""Article_\d{3}\.txt""")

    fun parseArticle(text: String): DiaryEntry? {
        val lines = text.replace("\r\n", "\n").split('\n')

        val dateLineIndex = lines.indexOfFirst { dateRegex.containsMatchIn(it) }
        if (dateLineIndex < 0) return null

        val match = dateRegex.find(lines[dateLineIndex]) ?: return null
        val (y, m, d) = match.groupValues.drop(1).map { it.toInt() }
        val date = LocalDate.of(y, m, d)

        // Body: everything after the date line, skipping initial blank lines
        var bodyStart = dateLineIndex + 1
        while (bodyStart < lines.size && lines[bodyStart].isBlank()) bodyStart++
        val body = lines.drop(bodyStart).joinToString("\n").trimEnd()

        return DiaryEntry(date = date, body = body)
    }

    fun loadFromAssets(context: Context, folder: String = "articles"): List<DiaryEntry> {
        val assetManager = context.assets
        val names = assetManager.list(folder)?.toList().orEmpty()

        val entries = mutableListOf<DiaryEntry>()

        // 1) Load from subfolders like articles/Story_004/Article_001.txt
        for (name in names) {
            if (name.endsWith(".txt", ignoreCase = true)) continue
            val sub = "$folder/$name"
            val subNames = assetManager.list(sub)?.toList().orEmpty()
            val articles = subNames.filter { it.matches(articleNameRegex) }.sorted()
            for (a in articles) {
                val text = assetManager.open("$sub/$a").bufferedReader(Charsets.UTF_8).use { it.readText() }
                val parsed = parseArticle(text) ?: continue
                // Optional photos: assets/photos/<Story_xxx>/*
                val photosFolder = "photos/$name"
                val photoNames = assetManager.list(photosFolder)?.toList().orEmpty()
                val photos = photoNames
                    .filter { it.lowercase().endsWith(".jpg") || it.lowercase().endsWith(".jpeg") || it.lowercase().endsWith(".png") || it.lowercase().endsWith(".webp") }
                    .sorted()
                    .map { "$photosFolder/$it" }
                entries.add(parsed.copy(photos = photos))
            }
        }

        // 2) Back-compat: load flat files directly under articles/
        val flat = names.filter { it.matches(articleNameRegex) }.sorted()
        for (a in flat) {
            val text = assetManager.open("$folder/$a").bufferedReader(Charsets.UTF_8).use { it.readText() }
            parseArticle(text)?.let(entries::add)
        }

        // If multiple entries share the same date, keep the first one.
        return entries.distinctBy { it.date }
    }
}

