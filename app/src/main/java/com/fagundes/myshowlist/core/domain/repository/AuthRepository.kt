package com.fagundes.myshowlist.core.domain.repository

import com.fagundes.myshowlist.core.domain.AuthUser

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    fun currentUser(): AuthUser?

    fun signOut()
}
