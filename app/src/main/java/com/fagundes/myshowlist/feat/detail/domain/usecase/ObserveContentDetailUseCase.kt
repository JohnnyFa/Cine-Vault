package com.fagundes.myshowlist.feat.detail.domain.usecase

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail
import com.fagundes.myshowlist.feat.detail.domain.repository.DetailRepository
import kotlinx.coroutines.flow.Flow

class ObserveContentDetailUseCase(
    private val repository: DetailRepository,
) {
    operator fun invoke(
        id: Int,
        type: ContentType,
    ): Flow<ContentDetail?> = repository.observeContentDetail(id, type)
}
