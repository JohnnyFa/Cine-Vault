package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.feat.home.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesCountUseCase(
    private val favoriteRepository: FavoriteRepository,
) {
    operator fun invoke(): Flow<Int> = favoriteRepository.observeCount()
}
