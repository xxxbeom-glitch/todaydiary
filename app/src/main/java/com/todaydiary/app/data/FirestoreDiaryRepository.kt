package com.todaydiary.app.data

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp
import com.todaydiary.app.ui.models.DiaryEntry
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.UUID

class FirestoreDiaryRepository(
    private val db: FirebaseFirestore = FirestoreInstances.diary,
) {
    private fun userDiaries(uid: String) = db.collection("users").document(uid).collection("diaries")

    /** `diaries`에서 지운 본문 스냅샷(원 필드 + [deletedAt]). 콘솔 Rules에 이 하위 컬렉션 쓰기 권한을 열어야 함. */
    private fun userDeletedDiaries(uid: String) = db.collection("users").document(uid).collection("deletedDiaries")

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
                    @Suppress("UNCHECKED_CAST")
                    val photos = (doc.get("photos") as? List<*>)
                        ?.mapNotNull { (it as? String)?.takeIf { s -> s.isNotBlank() } }
                        ?: emptyList()
                    val entry = DiaryEntry(id = id, date = date, body = body, photos = photos)
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
        writtenAt: Instant? = null,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {},
    ): Task<Void> {
        val docId = if (entry.id.isNotBlank()) {
            entry.id
        } else {
            // 레거시(날짜=문서ID) 호환: id가 없으면 날짜로 저장
            entry.date.toString()
        }

        val nowInstant = writtenAt ?: Instant.now()
        val ts = Timestamp(nowInstant.epochSecond, nowInstant.nano)
        val zone = ZoneId.systemDefault()
        val zdt = nowInstant.atZone(zone)
        val writtenAtText = zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val payload = mutableMapOf<String, Any>(
            "userId" to uid,
            "date" to entry.date.toString(),
            "year" to entry.date.year,
            "month" to entry.date.monthValue,
            "day" to entry.date.dayOfMonth,
            "writtenAt" to ts,
            "writtenAtText" to writtenAtText,
            "writtenAtTimeZone" to zone.id,
            "body" to entry.body,
            "photos" to entry.photos,
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        if (entry.id.isNotBlank()) {
            payload["id"] = entry.id
        } else {
            payload["id"] = docId
        }

        return userDiaries(uid).document(docId).set(
            payload
        , SetOptions.merge())
            .addOnSuccessListener {
                Log.i("FirestoreDiary", "saveEntry success users/$uid/diaries/$docId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreDiary", "saveEntry failed uid=$uid id=$docId date=${entry.date}", e)
                onFailure(e)
            }
    }

    fun newEntryId(): String = UUID.randomUUID().toString()

    /**
     * 같은 [date] + 같은 [body] 문자열인 **완전 중복** 문서만 1건만 남기고 나머지 문서(id)를 삭제.
     * (백업 가져오기 2회 등) 유지: [updatedAt]·[writtenAt] 최신, [photos] 많고, [id]는 안정적 타이브레이크.
     * @return 삭제된 문서 수
     */
    suspend fun removeExactDuplicateDiaries(uid: String): Int {
        val snap = userDiaries(uid).get().await()
        if (snap.documents.isEmpty()) return 0

        data class Item(
            val ref: com.google.firebase.firestore.DocumentReference,
            val updated: Long,
            val written: Long,
            val photosCount: Int,
            val id: String,
        )

        val byKey = mutableMapOf<Pair<LocalDate, String>, MutableList<Item>>()
        for (d in snap.documents) {
            val dateS = d.getString("date") ?: continue
            val bodyS = d.getString("body") ?: ""
            val date = runCatching { LocalDate.parse(dateS) }.getOrNull() ?: continue
            val key = date to DiaryBodyDedupe.normForDedupe(bodyS)
            byKey.getOrPut(key) { mutableListOf() }.add(
                Item(
                    ref = d.reference,
                    updated = d.getTimestamp("updatedAt")?.toDate()?.time ?: 0L,
                    written = d.getTimestamp("writtenAt")?.toDate()?.time ?: 0L,
                    photosCount = (d.get("photos") as? List<*>)?.size ?: 0,
                    id = d.id,
                )
            )
        }
        val toDelete = buildList {
            for (list in byKey.values) {
                if (list.size < 2) continue
                val sorted = list.sortedWith(
                    compareByDescending<Item> { it.updated }
                        .thenByDescending { it.written }
                        .thenByDescending { it.photosCount }
                        .thenBy { it.id }
                )
                for (i in 1 until sorted.size) {
                    add(sorted[i].ref)
                }
            }
        }
        if (toDelete.isEmpty()) return 0
        var removed = 0
        toDelete.chunked(500).forEach { chunk ->
            val b = db.batch()
            for (r in chunk) b.delete(r)
            b.commit().await()
            removed += chunk.size
        }
        Log.i("FirestoreDiary", "removeExactDuplicateDiaries: removed $removed for uid=$uid")
        return removed
    }

    /**
     * 일기를 삭제하기 전, 동일 [docId]로 [users/uid/deletedDiaries]에 복사한 뒤 [users/uid/diaries]에서 제거(트랜잭션).
     * 문서가 이미 없으면 아무 일도 하지 않음(성공).
     */
    suspend fun deleteEntry(uid: String, id: String, date: LocalDate) {
        val docId = if (id.isNotBlank()) id else date.toString()
        val dRef = userDiaries(uid).document(docId)
        val delRef = userDeletedDiaries(uid).document(docId)
        db.runTransaction(Transaction.Function {
            val s = it.get(dRef)!!
            if (!s.exists()) return@Function null
            val m = s.data?.toMutableMap() ?: mutableMapOf()
            m["deletedAt"] = FieldValue.serverTimestamp()
            m.putIfAbsent("userId", uid)
            it.set(delRef, m)
            it.delete(dRef)
            null
        }).await()
        Log.i("FirestoreDiary", "deleteEntry: users/$uid/deletedDiaries/$docId + removed from diaries")
    }
}

