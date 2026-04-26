package com.todaydiary.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate

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
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    val dateStr = doc.getString("date") ?: return@mapNotNull null
                    val body = doc.getString("body") ?: ""
                    runCatching { LocalDate.parse(dateStr) }.getOrNull()?.let { date ->
                        DiaryEntry(date = date, body = body)
                    }
                }
                onUpdate(list)
            }
    }

    fun saveEntry(uid: String, entry: DiaryEntry) {
        userDiaries(uid).document(entry.date.toString()).set(
            mapOf(
                "date" to entry.date.toString(),
                "body" to entry.body,
                "photos" to entry.photos,
                // 동기화/충돌해결에 필요한 메타데이터
                "updatedAt" to FieldValue.serverTimestamp(),
            )
        , SetOptions.merge())
    }

    fun deleteEntry(uid: String, date: LocalDate) {
        userDiaries(uid).document(date.toString()).delete()
    }
}

