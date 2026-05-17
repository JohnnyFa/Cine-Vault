package com.fagundes.myshowlist.feat.detail.data.repository

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.detail.domain.ContentDetailUi
import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun observeContentDetail(id: Int, type: String): Flow<ContentDetailUi?>
    suspend fun refreshDetailIfNeeded(id: Int)
    suspend fun cacheFavoriteCandidate(item: FavoriteItem)
    fun observeFavoriteState(id: Int, type: ContentType): Flow<Boolean>
    suspend fun toggleFavorite(id: Int, type: ContentType): Result<Boolean>
}
