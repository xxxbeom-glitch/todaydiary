package com.todaydiary.app.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate
import java.util.UUID

class FirestoreDiaryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private fun userDiaries(uid: String) = db.collection("users").document(uid).collection("diaries")

    fun listenEntries(
        uid: String,
        onUpdate: (List<DiaryEntry>) -> Unit,
        onError: (Exception) -> Unit = {},
    ): ListenerRegistration {
        return userDiaries(uid)
            // NOTE: 과거 문서에 updatedAt이 없을 수 있어, 서버 쿼리 orderBy에 의존하지 않는다.
            // 전부 받고 앱 쪽에서 updatedAt(없으면 0) + date로 정렬
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }
                data class Row(val entry: DiaryEntry, val updatedAtMs: Long, val date: LocalDate)
                val rows = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    val dateStr = doc.getString("date") ?: return@mapNotNull null
                    val body = doc.getString("body") ?: ""
                    val idFromField = doc.getString("id")
                    val id = when {
                        !idFromField.isNullOrBlank() -> idFromField
                        !doc.id.isNullOrBlank() -> doc.id
                        else -> ""
                    }
                    val date = runCatching { LocalDate.parse(dateStr) }.getOrNull() ?: return@mapNotNull null
                    val updatedAtMs = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
                    val entry = DiaryEntry(id = id, date = date, body = body)
                    Row(entry = entry, updatedAtMs = updatedAtMs, date = date)
                }.sortedWith(
                    compareByDescending<Row> { it.updatedAtMs }
                        .thenByDescending { it.date }
                        .thenByDescending { it.entry.id }
                )
                onUpdate(rows.map { it.entry })
            }
    }

    fun saveEntry(
        uid: String,
        entry: DiaryEntry,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {},
    ) {
        val docId = if (entry.id.isNotBlank()) {
            entry.id
        } else {
            // 레거시(날짜=문서ID) 호환: id가 없으면 날짜로 저장
            entry.date.toString()
        }

        val payload = mutableMapOf<String, Any>(
            "date" to entry.date.toString(),
            "body" to entry.body,
            "photos" to entry.photos,
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        if (entry.id.isNotBlank()) {
            payload["id"] = entry.id
        } else {
            payload["id"] = docId
        }

        userDiaries(uid).document(docId).set(
            payload
        , SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("FirestoreDiary", "saveEntry failed uid=$uid id=$docId date=${entry.date}", e)
                onFailure(e)
            }
    }

    fun newEntryId(): String = UUID.randomUUID().toString()

    fun deleteEntry(
        uid: String,
        id: String,
        date: LocalDate,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {},
    ) {
        val docId = if (id.isNotBlank()) id else date.toString()
        userDiaries(uid).document(docId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("FirestoreDiary", "deleteEntry failed uid=$uid id=$docId", e)
                onFailure(e)
            }
    }
}

