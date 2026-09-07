package com.fagundes.myshowlist.core.data.local.datasource

import com.fagundes.myshowlist.core.data.local.dao.MovieDetailCacheDao
import com.fagundes.myshowlist.core.data.local.entity.CachedMovieDetailEntity
import kotlinx.coroutines.flow.Flow

class DetailCacheLocalDataSourceImpl(
    private val dao: MovieDetailCacheDao,
) : DetailCacheLocalDataSource {
    override fun observeDetail(id: Int): Flow<CachedMovieDetailEntity?> = dao.observeMovieById(id)

    override suspend fun getDetail(id: Int): CachedMovieDetailEntity? = dao.getMovieById(id)

    override suspend fun saveDetail(detail: CachedMovieDetailEntity) {
        dao.upsert(detail)
    }

    override suspend fun clearExpired(olderThan: Long) {
        dao.deleteExpiredCache(olderThan)
    }

    override suspend fun clearAll() {
        dao.deleteAll()
    }
}
