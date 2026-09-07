package com.fagundes.myshowlist.feat.home.domain.usecase

import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.feat.home.data.repository.RecentRepository

class SaveRecentMovieUseCase(
    private val repository: RecentRepository,
) {
    suspend operator fun invoke(movie: ContentItem) = repository.saveRecent(movie)
}
