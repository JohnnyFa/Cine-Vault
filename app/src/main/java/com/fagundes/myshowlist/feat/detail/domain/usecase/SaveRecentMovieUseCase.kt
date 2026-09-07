package com.fagundes.myshowlist.feat.detail.domain.usecase

import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.core.domain.repository.RecentRepository

class SaveRecentMovieUseCase(
    private val repository: RecentRepository,
) {
    suspend operator fun invoke(item: ContentItem) = repository.saveRecent(item)
}
