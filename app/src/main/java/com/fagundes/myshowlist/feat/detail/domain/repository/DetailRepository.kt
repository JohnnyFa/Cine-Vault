package com.fagundes.myshowlist.feat.detail.domain.repository

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun observeContentDetail(
        id: Int,
        type: ContentType,
    ): Flow<ContentDetail?>

    suspend fun refreshDetailIfNeeded(id: Int): Result<Unit>

    fun observeFavoriteState(
        id: Int,
        type: ContentType,
    ): Flow<Boolean>

    suspend fun toggleFavorite(item: ContentItem): Result<Boolean>
}
