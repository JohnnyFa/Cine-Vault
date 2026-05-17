package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.domain.Movie
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observePopularMovies(): Flow<List<Movie>>
    fun observeRecommendedMovies(): Flow<List<Movie>>
    fun observeShowOfTheDay(): Flow<Movie?>
    suspend fun refreshHomeIfNeeded()
}
