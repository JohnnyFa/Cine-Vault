package com.fagundes.myshowlist.feat.home.domain.usecase

import com.fagundes.myshowlist.feat.home.domain.repository.HomeRepository

class RefreshHomeUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.refreshHomeIfNeeded()
}
