package com.todaydiary.app.data

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreUserRepository(
    private val db: FirebaseFirestore = FirestoreInstances.diary,
) {
    private fun userDoc(uid: String) = db.collection("users").document(uid)

    /**
     * 로그인 직후 호출: `users/{uid}` 프로필 문서를 (없으면) 생성하고, 마지막 로그인 시간을 갱신합니다.
     */
    fun ensureUserDocument(
        user: FirebaseUser,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {},
    ) {
        val uid = user.uid
        userDoc(uid).get()
            .addOnSuccessListener { snap ->
                val now = FieldValue.serverTimestamp()
                val base = mapOf(
                    "uid" to uid,
                    "email" to (user.email ?: ""),
                    "displayName" to (user.displayName ?: ""),
                    "photoUrl" to (user.photoUrl?.toString() ?: ""),
                    "updatedAt" to now,
                    "lastLoginAt" to now,
                )

                val payload =
                    if (!snap.exists()) {
                        base + mapOf("createdAt" to now)
                    } else {
                        base
                    }

                userDoc(uid)
                    .set(payload, SetOptions.merge())
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreUser", "ensureUserDocument set failed uid=$uid", e)
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreUser", "ensureUserDocument get failed uid=$uid", e)
                onFailure(e)
            }
    }
}
