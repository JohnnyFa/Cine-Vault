package com.fagundes.myshowlist.core.data.repository

import com.fagundes.myshowlist.core.domain.AuthUser
import com.fagundes.myshowlist.core.domain.repository.AuthRepository
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

    override fun currentUser(): AuthUser? =
        firebaseAuth.currentUser?.let { user ->
            AuthUser(
                displayName = user.displayName,
                email = user.email,
                photoUrl = user.photoUrl?.toString(),
            )
        }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
