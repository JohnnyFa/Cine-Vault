package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.feat.home.data.repository.FavoriteRepository

class ClearFavoritesUseCase(
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke() = favoriteRepository.clearAll()
}
