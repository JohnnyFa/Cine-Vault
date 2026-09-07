package com.fagundes.myshowlist.core.data.repository

import com.fagundes.myshowlist.core.data.local.datasource.RecentLocalDataSource
import com.fagundes.myshowlist.core.data.local.mapper.toMovie
import com.fagundes.myshowlist.core.data.local.mapper.toRecentEntity
import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.core.domain.repository.RecentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentRepositoryImpl(
    private val local: RecentLocalDataSource,
) : RecentRepository {
    override fun observeRecents(): Flow<List<Movie>> = local.observeRecents().map { entities -> entities.map { it.toMovie() } }

    override suspend fun saveRecent(item: ContentItem) {
        local.save(item.toRecentEntity(viewedAt = System.currentTimeMillis()))
        local.trimToLimit()
    }

    override fun observeCount(): Flow<Int> = local.observeCount()

    override suspend fun clearAll() {
        local.clearAll()
    }
}
