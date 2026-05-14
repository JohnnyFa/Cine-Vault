package com.fagundes.myshowlist.feat.detail.domain.usecase

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.detail.data.repository.DetailRepository

class ToggleFavoriteUseCase(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(itemId: Int, type: ContentType): Result<Boolean> =
        repository.toggleFavorite(itemId, type)
}
