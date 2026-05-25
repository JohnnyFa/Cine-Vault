package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.domain.Movie
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeAllFavorites(): Flow<List<Movie>>

    suspend fun clearAll()
}
