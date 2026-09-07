package com.fagundes.myshowlist.feat.login.domain.repository

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
}
