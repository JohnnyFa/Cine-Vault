package com.fagundes.myshowlist.core.domain.repository

import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.core.domain.Movie
import kotlinx.coroutines.flow.Flow

interface RecentRepository {
    fun observeRecents(): Flow<List<Movie>>

    fun observeCount(): Flow<Int>

    suspend fun saveRecent(item: ContentItem)

    suspend fun clearAll()
}
