package com.fagundes.myshowlist.core.domain.repository

import com.fagundes.myshowlist.core.domain.AuthSession

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    fun currentSession(): AuthSession

    fun continueAsGuest()

    fun signOut()
}
