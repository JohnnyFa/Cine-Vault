package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface RecentRepository {
    fun observeRecents(): Flow<List<Movie>>

    fun observeCount(): Flow<Int>

    suspend fun saveRecent(movie: FavoriteItem)

    suspend fun clearAll()
}
