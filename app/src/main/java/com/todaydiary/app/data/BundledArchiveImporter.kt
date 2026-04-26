package com.todaydiary.app.data

import android.content.Context
import com.todaydiary.app.ui.import.PencakeImporter
import kotlinx.coroutines.tasks.await

object BundledArchiveImporter {

    /**
     * APK [assets]에 포함된 `restore/Story_*` (Gradle로 루트에 병합) 일기를 Firestore에 **새 문서 ID**로 일괄 저장.
     * - 이미지는 [DiaryPhotoStorage] → Storage `diary-photo/uid/entryId/...`에 올리고, Firestore에는 **https URL**을 저장.
     */
    suspend fun importBundledRestoreToFirestore(
        context: Context,
        uid: String,
        repo: FirestoreDiaryRepository = FirestoreDiaryRepository(),
    ): Int {
        val entries = PencakeImporter.loadFromBundledRestore(context)
        if (entries.isEmpty()) return 0
        var n = 0
        for (e in entries) {
            val id = repo.newEntryId()
            val photos = DiaryPhotoStorage.ensurePhotosInRemoteStorage(context, uid, id, e.photos)
            repo.saveEntry(uid, e.copy(id = id, photos = photos), writtenAt = null).await()
            n++
        }
        return n
    }
}
