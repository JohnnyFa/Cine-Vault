package com.fagundes.myshowlist.feat.home.domain.usecase

import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import com.fagundes.myshowlist.feat.home.data.repository.RecentRepository

class SaveRecentMovieUseCase(
    private val repository: RecentRepository
) {
    suspend operator fun invoke(movie: FavoriteItem) = repository.saveRecent(movie)
}
