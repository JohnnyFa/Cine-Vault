package com.fagundes.myshowlist.feat.login.data

import com.fagundes.myshowlist.feat.login.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->

            val credential =
                GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth
                .signInWithCredential(credential)
                .addOnSuccessListener {
                    cont.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(e))
                }
        }
}
