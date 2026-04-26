package com.todaydiary.app.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun rememberFirebaseUserState(): State<FirebaseUser?> {
    val auth = remember { FirebaseAuth.getInstance() }
    val state = remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            state.value = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    return state
}

