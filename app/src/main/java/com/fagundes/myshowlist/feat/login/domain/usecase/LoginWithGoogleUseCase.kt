package com.fagundes.myshowlist.feat.login.domain.usecase

import com.fagundes.myshowlist.core.domain.repository.AuthRepository

class LoginWithGoogleUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): Result<Unit> = repository.signInWithGoogle(idToken)
}
