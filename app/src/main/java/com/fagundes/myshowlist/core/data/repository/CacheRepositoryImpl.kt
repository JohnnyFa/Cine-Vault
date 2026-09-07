package com.fagundes.myshowlist.core.data.repository

import com.fagundes.myshowlist.core.data.local.datasource.ContentLocalDataSource
import com.fagundes.myshowlist.core.data.local.datasource.DetailCacheLocalDataSource
import com.fagundes.myshowlist.core.domain.repository.CacheRepository

class CacheRepositoryImpl(
    private val content: ContentLocalDataSource,
    private val detailCache: DetailCacheLocalDataSource,
) : CacheRepository {
    override suspend fun clearAll() {
        content.clearAll()
        detailCache.clearAll()
    }
}
