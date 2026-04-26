package com.todaydiary.app.data

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Storage 경로: `diary-photo/{uid}/{entryId}/파일명` — Firestore photos에는 **다운로드 https**만. 이미 https/gs면 유지.
 */
object DiaryPhotoStorage {
    const val FOLDER = "diary-photo"

    private val cache = ConcurrentHashMap<String, String>()

    private fun cacheKey(entryId: String, localRef: String) = "$entryId|$localRef"

    suspend fun ensurePhotosInRemoteStorage(
        context: Context,
        uid: String,
        entryId: String,
        photos: List<String>,
    ): List<String> = withContext(Dispatchers.IO) {
        if (photos.isEmpty()) return@withContext emptyList()
        photos.map { p -> toDownloadUrlOrKeep(context, uid, entryId, p) }
    }

    private fun isAlreadyRemote(s: String): Boolean {
        val t = s.trim()
        if (t.isEmpty()) return true
        return t.startsWith("https://", ignoreCase = true) ||
            t.startsWith("http://", ignoreCase = true) ||
            t.startsWith("gs://", ignoreCase = true) ||
            t.contains("firebasestorage", ignoreCase = true)
    }

    private suspend fun toDownloadUrlOrKeep(
        context: Context,
        uid: String,
        entryId: String,
        raw: String,
    ): String {
        val p = raw.trim()
        if (p.isEmpty()) return p
        if (isAlreadyRemote(p)) return p
        cache[cacheKey(entryId, p)]?.let { return it }

        val opened = openStreamWithLabel(context, p)
        val contentType = guessContentTypeFromName(opened.label)
        val unique = UUID.randomUUID().toString().take(8)
        val safe = "${unique}_${
            if (opened.label.isNotBlank()) {
                val n = opened.label.substringAfterLast('/')
                n.filter { it.isLetterOrDigit() || it == '.' || it == '_' || it == '-' }
                    .ifEmpty { "image" }
                    .take(120)
            } else "image"
        }"

        val ref: StorageReference = FirebaseStorage.getInstance().reference
            .child(FOLDER)
            .child(uid)
            .child(entryId)
            .child(safe)
        val meta = StorageMetadata.Builder().setContentType(contentType).build()
        opened.input.use { s ->
            ref.putStream(s, meta).await()
        }
        val https = ref.downloadUrl.await().toString()
        cache[cacheKey(entryId, p)] = https
        return https
    }

    private data class Opened(val input: java.io.InputStream, val label: String)

    private fun openStreamWithLabel(context: Context, p: String): Opened = when {
        p.startsWith("file://", true) -> {
            val path = Uri.parse(p).path ?: p.removePrefix("file://")
            val f = File(path)
            if (!f.isFile) throw java.io.FileNotFoundException("file: $p")
            Opened(FileInputStream(f), f.name)
        }
        p.startsWith("content://", true) -> {
            val uri = Uri.parse(p)
            val s = context.contentResolver.openInputStream(uri)
                ?: throw java.io.FileNotFoundException("content: $p")
            Opened(s, uri.lastPathSegment ?: "image")
        }
        p.startsWith("/") && File(p).isFile -> {
            val f = File(p)
            Opened(FileInputStream(f), f.name)
        }
        else -> {
            val s = try {
                context.assets.open(p)
            } catch (e: java.io.FileNotFoundException) {
                throw (java.io.FileNotFoundException("에셋·파일을 열 수 없습니다: $p").apply { initCause(e) })
            }
            Opened(s, p)
        }
    }

    private fun guessContentTypeFromName(name: String): String {
        val ext = name.substringAfterLast('.', "jpg").lowercase()
        return when (ext) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            else -> "image/jpeg"
        }
    }
}
