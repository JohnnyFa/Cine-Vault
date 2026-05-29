package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.feat.home.data.repository.RecentRepository

class ClearRecentsUseCase(
    private val recentRepository: RecentRepository,
) {
    suspend operator fun invoke() = recentRepository.clearAll()
}
