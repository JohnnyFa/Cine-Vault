package com.fagundes.myshowlist.core.data.repository

import com.fagundes.myshowlist.core.data.local.datasource.GuestSessionLocalDataSource
import com.fagundes.myshowlist.core.domain.AuthSession
import com.fagundes.myshowlist.core.domain.AuthUser
import com.fagundes.myshowlist.core.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val guestSession: GuestSessionLocalDataSource,
) : AuthRepository {
    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->

            val credential =
                GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth
                .signInWithCredential(credential)
                .addOnSuccessListener {
                    guestSession.setGuest(false)
                    cont.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(e))
                }
        }

    // A Firebase user wins over a stale guest flag.
    override fun currentSession(): AuthSession {
        val user = firebaseAuth.currentUser
        return when {
            user != null ->
                AuthSession.SignedIn(
                    AuthUser(
                        displayName = user.displayName,
                        email = user.email,
                        photoUrl = user.photoUrl?.toString(),
                    ),
                )
            guestSession.isGuest() -> AuthSession.Guest
            else -> AuthSession.SignedOut
        }
    }

    override fun continueAsGuest() {
        guestSession.setGuest(true)
    }

    override fun signOut() {
        firebaseAuth.signOut()
        guestSession.setGuest(false)
    }
}
