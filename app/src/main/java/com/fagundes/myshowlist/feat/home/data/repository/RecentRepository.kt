package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.core.domain.Movie
import kotlinx.coroutines.flow.Flow

interface RecentRepository {
    fun observeRecents(): Flow<List<Movie>>

    fun observeCount(): Flow<Int>

    suspend fun saveRecent(movie: ContentItem)

    suspend fun clearAll()
}
