package com.fagundes.myshowlist.core.data.local.datasource

import com.fagundes.myshowlist.core.data.local.entity.CachedMovieDetailEntity
import kotlinx.coroutines.flow.Flow

interface DetailCacheLocalDataSource {
    fun observeDetail(id: Int): Flow<CachedMovieDetailEntity?>

    suspend fun getDetail(id: Int): CachedMovieDetailEntity?

    suspend fun saveDetail(detail: CachedMovieDetailEntity)

    suspend fun clearExpired(olderThan: Long)

    suspend fun clearAll()
}
