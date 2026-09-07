package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.core.domain.repository.FavoriteRepository
import com.fagundes.myshowlist.core.domain.repository.RecentRepository

class ClearUserDataUseCase(
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
) {
    suspend operator fun invoke() {
        favoriteRepository.clearAll()
        recentRepository.clearAll()
    }
}
