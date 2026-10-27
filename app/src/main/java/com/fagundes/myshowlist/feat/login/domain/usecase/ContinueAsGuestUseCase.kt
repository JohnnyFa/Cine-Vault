package com.fagundes.myshowlist.feat.login.domain.usecase

import com.fagundes.myshowlist.core.domain.repository.AuthRepository

class ContinueAsGuestUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke() = repository.continueAsGuest()
}
