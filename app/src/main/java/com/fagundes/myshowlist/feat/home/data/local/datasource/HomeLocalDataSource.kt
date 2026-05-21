package com.fagundes.myshowlist.feat.home.data.local.datasource

import com.fagundes.myshowlist.core.data.local.entity.ContentEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import kotlinx.coroutines.flow.Flow

interface HomeLocalDataSource {
    suspend fun saveMoviesForCategory(
        category: ContentCategory,
        items: List<ContentEntity>,
    )

    fun observeMoviesByCategory(category: ContentCategory): Flow<List<ContentEntity>>

    suspend fun getMoviesByCategory(category: ContentCategory): List<ContentEntity>

    suspend fun clearExpired(olderThan: Long)
}
