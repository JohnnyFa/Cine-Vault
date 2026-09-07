package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.core.domain.repository.RecentRepository
import kotlinx.coroutines.flow.Flow

class ObserveRecentsCountUseCase(
    private val recentRepository: RecentRepository,
) {
    operator fun invoke(): Flow<Int> = recentRepository.observeCount()
}
