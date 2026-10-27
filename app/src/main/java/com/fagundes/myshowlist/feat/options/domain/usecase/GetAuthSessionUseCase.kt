package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.core.domain.AuthSession
import com.fagundes.myshowlist.core.domain.repository.AuthRepository

class GetAuthSessionUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke(): AuthSession = repository.currentSession()
}
