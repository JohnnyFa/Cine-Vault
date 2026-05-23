package com.fagundes.myshowlist.feat.detail.domain.usecase

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.detail.data.repository.DetailRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteStateUseCase(
    private val repository: DetailRepository,
) {
    operator fun invoke(
        itemId: Int,
        type: ContentType,
    ): Flow<Boolean> = repository.observeFavoriteState(itemId, type)
}
