package com.todaydiary.app.ui.import

import android.content.Context
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate

object PencakeImporter {
    private val dateRegex = Regex("""(\d{4})년\s*(\d{1,2})월\s*(\d{1,2})일""")
    private val articleNameRegex = Regex("""Article_(\d{3})\.txt""")
    /** [assets] 루트(merged)에 붙는 Pencake 백업 [Story_001] 등 */
    private val storyFolderRegex = Regex("""^Story_\d{3}$""")

    /** 머리줄(날짜·시간 줄 앞)에서 [아침일기] [저녁일기]를 찾는다(예: `8월 12일 아침일기` → [아침일기]만 본문에). */
    private fun partLabelFromTitleLine(line: String): String? = when {
        line.contains("아침일기") -> "아침일기"
        line.contains("저녁일기") -> "저녁일기"
        else -> null
    }

    data class ArticlePart(
        val date: LocalDate,
        val body: String,
        val partLabel: String?,
        val articleOrder: Int,
        val photos: List<String> = emptyList(),
    )

    private fun articleIndex(articleFileName: String): Int =
        articleNameRegex.matchEntire(articleFileName)?.groupValues?.get(1)?.toIntOrNull() ?: 0

    /**
     * [Article_*.txt] 한 개 파싱.
     * * 날짜·시간: 첫 [dateRegex] 줄(보통 3행).
     * * 아침/저녁: **날짜 줄 앞** 마지막 비어 있지 않은 머리에서 `아침일기` [저녁일기] 검사(예: `8월 12일 아침일기`).
     */
    fun parseArticlePart(text: String, articleFileName: String = ""): ArticlePart? {
        val lines = text.replace("\r\n", "\n").split('\n')
        val dateLineIndex = lines.indexOfFirst { dateRegex.containsMatchIn(it) }
        if (dateLineIndex < 0) return null
        val match = dateRegex.find(lines[dateLineIndex]) ?: return null
        val (y, m, d) = match.groupValues.drop(1).map { it.toInt() }
        val date = LocalDate.of(y, m, d)
        val titleBeforeDate = lines
            .take(dateLineIndex)
            .map { it.trim() }
            .lastOrNull { it.isNotEmpty() }
        val partLabel = titleBeforeDate?.let { partLabelFromTitleLine(it) }
        var bodyStart = dateLineIndex + 1
        while (bodyStart < lines.size && lines[bodyStart].isBlank()) bodyStart++
        val body = lines.drop(bodyStart).joinToString("\n").trimEnd()
        val order = if (articleFileName.isNotEmpty()) articleIndex(articleFileName) else 0
        return ArticlePart(date, body, partLabel, order, emptyList())
    }

    /** 단일 [Article_*.txt] → [DiaryEntry] (머리줄 [아침일기] 등은 `body`에 **넣지 않음**; 합치기는 [mergeSameDateParts]) */
    fun parseArticle(text: String): DiaryEntry? {
        val p = parseArticlePart(text) ?: return null
        return DiaryEntry(date = p.date, body = p.body, photos = p.photos)
    }

    private fun partSortOrder(label: String?): Int = when (label) {
        "아침일기" -> 0
        "저녁일기" -> 1
        else -> 2
    }

    private fun buildMergedBody(partList: List<ArticlePart>): String {
        if (partList.isEmpty()) return ""
        if (partList.size == 1) return partList[0].body
        return partList
            .sortedWith(
                compareBy<ArticlePart> { partSortOrder(it.partLabel) }
                    .thenBy { it.articleOrder }
            )
            .joinToString("\n\n") { p ->
                if (p.partLabel != null) {
                    "${p.partLabel}\n${p.body}"
                } else {
                    p.body
                }
            }
    }

    /** 같은 날 [Article_*.txt]를 한 건으로(아침+저녁은 머리+본문). */
    fun mergeSameDateParts(parts: List<ArticlePart>): List<DiaryEntry> {
        if (parts.isEmpty()) return emptyList()
        return parts
            .groupBy { it.date }
            .map { (_, oneDay) ->
                val merged = buildMergedBody(oneDay)
                val photos = oneDay.asSequence()
                    .flatMap { it.photos.asSequence() }
                    .distinct()
                    .toList()
                val first = oneDay.minByOrNull { it.articleOrder }!!
                DiaryEntry(
                    date = first.date,
                    body = merged,
                    photos = photos,
                )
            }
            .sortedWith(compareByDescending<DiaryEntry> { it.date })
    }

    private fun addArticleParts(
        text: String,
        name: String,
        photosRel: List<String>,
        out: MutableList<ArticlePart>,
    ) {
        val p = parseArticlePart(text, name) ?: return
        out.add(p.copy(photos = photosRel))
    }

    /**
     * 각 [Story_xxx] 아래의 이미지 — `Photos`, `Images` 등(에셋은 대소문자 구분 가능성이 있어 [photos] 소문자도 시도).
     * 루트 `photos/Story_xxx`(레거시)는 별도.
     */
    private fun listImageAssetPathsForStory(
        assetManager: android.content.res.AssetManager,
        story: String,
    ): List<String> {
        val out = linkedSetOf<String>()
        for (imageSub in listOf("Images", "Photos", "Photo", "Image", "Pictures", "photos", "pictures")) {
            val p = "$story/$imageSub"
            val names = assetManager.list(p) ?: continue
            for (f in names) {
                if (f.endsWithAnyIgnoreCase()) out.add("$p/$f")
            }
        }
        val leg = "photos/$story"
        for (f in assetManager.list(leg).orEmpty()) {
            if (f.endsWithAnyIgnoreCase()) out.add("$leg/$f")
        }
        return out.sorted()
    }

    /**
     * APK에 포함된 Pencake **restore** 트리 ([Story_\\d\\d\\d]/[Text/]Article_*.txt + 이미지).
     * [app/build.gradle.kts] [assets]에 `../restore` 가 들어갈 때 사용.
     */
    fun loadFromBundledRestore(context: Context): List<DiaryEntry> {
        val am = context.assets
        val roots = am.list("")?.toList().orEmpty()
        val partList = mutableListOf<ArticlePart>()
        for (name in roots) {
            if (!name.matches(storyFolderRegex)) continue
            val sub = name
            val subNames = am.list(sub)?.toList().orEmpty()
            val inText = subNames.any { it.equals("Text", ignoreCase = true) }
            val articleDir = if (inText) "$sub/Text" else sub
            val under = am.list(articleDir) ?: continue
            val articleFiles = under.filter { it.matches(articleNameRegex) }.sorted()
            val photos = listImageAssetPathsForStory(am, name)
            for (a in articleFiles) {
                val t = try {
                    am.open("$articleDir/$a").bufferedReader(Charsets.UTF_8).use { it.readText() }
                } catch (_: Exception) {
                    continue
                }
                addArticleParts(t, a, photos, partList)
            }
        }
        return mergeSameDateParts(partList)
    }

    fun loadFromAssets(context: Context, folder: String = "articles"): List<DiaryEntry> {
        val assetManager = context.assets
        val topNames = assetManager.list(folder)?.toList().orEmpty()
        val partList = mutableListOf<ArticlePart>()

        for (story in topNames) {
            if (story.endsWith(".txt", ignoreCase = true)) continue
            val sub = "$folder/$story"
            val subNames = assetManager.list(sub)?.toList().orEmpty()
            val inText = subNames.any { it.equals("Text", ignoreCase = true) }
            val articleDir = if (inText) "$sub/Text" else sub
            val underArticle = assetManager.list(articleDir) ?: continue
            val articleFiles = underArticle.filter { it.matches(articleNameRegex) }.sorted()
            val photosFolder = "photos/$story"
            val photoNames = assetManager.list(photosFolder)?.toList().orEmpty()
            val photos = photoNames
                .filter { it.endsWithAnyIgnoreCase() }
                .sorted()
                .map { "$photosFolder/$it" }
            for (a in articleFiles) {
                val t = try {
                    assetManager.open("$articleDir/$a").bufferedReader(Charsets.UTF_8).use { it.readText() }
                } catch (_: Exception) {
                    continue
                }
                addArticleParts(t, a, photos, partList)
            }
        }

        val flat = topNames.filter { it.matches(articleNameRegex) }.sorted()
        for (a in flat) {
            val t = try {
                assetManager.open("$folder/$a").bufferedReader(Charsets.UTF_8).use { it.readText() }
            } catch (_: Exception) {
                continue
            }
            addArticleParts(t, a, emptyList(), partList)
        }

        return mergeSameDateParts(partList)
    }
}

private fun String.endsWithAnyIgnoreCase() =
    lowercase().endsWith(".jpg") || lowercase().endsWith(".jpeg") || lowercase().endsWith(".png") || lowercase()
        .endsWith(".webp")
