package com.fagundes.myshowlist.feat.detail.data.local

import com.fagundes.myshowlist.core.data.local.entity.CachedMovieDetailEntity
import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import kotlinx.coroutines.flow.Flow

interface DetailLocalDataSource {
    fun observeDetail(id: Int): Flow<CachedMovieDetailEntity?>

    suspend fun getDetail(id: Int): CachedMovieDetailEntity?

    suspend fun saveDetail(detail: CachedMovieDetailEntity)

    suspend fun clearExpiredDetails(olderThan: Long)

    fun observeFavorite(
        id: Int,
        type: ContentType,
    ): Flow<FavoriteEntity?>

    suspend fun isFavorite(
        id: Int,
        type: ContentType,
    ): Boolean

    suspend fun addFavorite(favorite: FavoriteEntity)

    suspend fun removeFavorite(
        id: Int,
        type: ContentType,
    )
}
