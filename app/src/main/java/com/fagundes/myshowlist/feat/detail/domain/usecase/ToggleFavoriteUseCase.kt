package com.fagundes.myshowlist.feat.detail.domain.usecase

import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.feat.detail.domain.repository.DetailRepository

class ToggleFavoriteUseCase(
    private val repository: DetailRepository,
) {
    /**
     * Takes the item to favourite so the repository holds no per-item state between calls.
     */
    suspend operator fun invoke(item: ContentItem): Result<Boolean> = repository.toggleFavorite(item)
}
