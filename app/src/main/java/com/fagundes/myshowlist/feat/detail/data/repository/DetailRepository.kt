package com.fagundes.myshowlist.feat.detail.data.repository

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.detail.domain.ContentDetailUi
import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    suspend fun getContentDetail(id: String, type: String): Result<ContentDetailUi>
    suspend fun cacheFavoriteCandidate(item: FavoriteItem)
    fun observeFavoriteState(id: Int, type: ContentType): Flow<Boolean>
    suspend fun toggleFavorite(id: Int, type: ContentType): Result<Boolean>
}
