package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.core.domain.AuthUser
import com.fagundes.myshowlist.core.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke(): AuthUser? = repository.currentUser()
}
