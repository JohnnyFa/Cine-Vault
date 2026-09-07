package com.fagundes.myshowlist.feat.detail.domain.usecase

import com.fagundes.myshowlist.feat.detail.domain.repository.DetailRepository

class RefreshContentDetailUseCase(
    private val repository: DetailRepository,
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.refreshDetailIfNeeded(id)
}
