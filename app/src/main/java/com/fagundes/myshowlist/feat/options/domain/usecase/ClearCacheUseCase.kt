package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.core.domain.repository.CacheRepository

class ClearCacheUseCase(
    private val cacheRepository: CacheRepository,
) {
    suspend operator fun invoke() = cacheRepository.clearAll()
}
