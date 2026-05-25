package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.feat.home.data.repository.FavoriteRepository
import com.fagundes.myshowlist.feat.home.data.repository.RecentRepository

class ClearUserDataUseCase(
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
) {
    suspend operator fun invoke() {
        favoriteRepository.clearAll()
        recentRepository.clearAll()
    }
}
