package com.fagundes.myshowlist.feat.home.data.local.datasource

import com.fagundes.myshowlist.core.data.local.dao.ContentDao
import com.fagundes.myshowlist.core.data.local.entity.ContentEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import kotlinx.coroutines.flow.Flow

class HomeLocalDataSourceImpl(
    private val dao: ContentDao
) : HomeLocalDataSource {

    override suspend fun saveMoviesForCategory(category: ContentCategory, items: List<ContentEntity>) {
        dao.replaceCategory(category)
        dao.insertAll(items)
    }

    override fun observeMoviesByCategory(category: ContentCategory): Flow<List<ContentEntity>> =
        dao.observeCategory(category)

    override suspend fun getMoviesByCategory(category: ContentCategory): List<ContentEntity> = dao.getCategory(category)

    override suspend fun clearExpired(olderThan: Long) {
        dao.deleteExpiredCache(olderThan)
    }
}
