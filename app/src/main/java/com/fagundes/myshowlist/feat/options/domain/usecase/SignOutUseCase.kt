package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.core.domain.repository.AuthRepository

class SignOutUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke() = repository.signOut()
}
