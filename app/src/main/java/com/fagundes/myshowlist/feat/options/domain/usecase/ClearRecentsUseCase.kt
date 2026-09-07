package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.core.domain.repository.RecentRepository

class ClearRecentsUseCase(
    private val recentRepository: RecentRepository,
) {
    suspend operator fun invoke() = recentRepository.clearAll()
}
