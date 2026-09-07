package com.fagundes.myshowlist.feat.detail.data.local

import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.data.local.datasource.DetailCacheLocalDataSource
import com.fagundes.myshowlist.core.data.local.entity.CachedMovieDetailEntity
import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import kotlinx.coroutines.flow.Flow

class DetailLocalDataSourceImpl(
    private val detailCache: DetailCacheLocalDataSource,
    private val favoriteDao: FavoriteDao,
) : DetailLocalDataSource {
    override fun observeDetail(id: Int): Flow<CachedMovieDetailEntity?> = detailCache.observeDetail(id)

    override suspend fun getDetail(id: Int): CachedMovieDetailEntity? = detailCache.getDetail(id)

    override suspend fun saveDetail(detail: CachedMovieDetailEntity) {
        detailCache.saveDetail(detail)
    }

    override suspend fun clearExpiredDetails(olderThan: Long) {
        detailCache.clearExpired(olderThan)
    }

    override fun observeFavorite(
        id: Int,
        type: ContentType,
    ): Flow<FavoriteEntity?> = favoriteDao.observeById(id, type)

    override suspend fun isFavorite(
        id: Int,
        type: ContentType,
    ): Boolean = favoriteDao.isFavorite(id, type)

    override suspend fun addFavorite(favorite: FavoriteEntity) {
        favoriteDao.upsert(favorite)
    }

    override suspend fun removeFavorite(
        id: Int,
        type: ContentType,
    ) {
        favoriteDao.remove(id, type)
    }
}
