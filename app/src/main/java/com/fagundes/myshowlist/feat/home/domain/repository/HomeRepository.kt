package com.fagundes.myshowlist.feat.home.domain.repository

import com.fagundes.myshowlist.core.domain.Movie
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observePopularMovies(): Flow<List<Movie>>

    fun observeRecommendedMovies(): Flow<List<Movie>>

    fun observeShowOfTheDay(): Flow<Movie?>

    suspend fun refreshHomeIfNeeded(): Result<Unit>

    suspend fun getRecommendationsFor(movieId: Int): Result<List<Movie>>

    suspend fun getTopRatedMovies(): Result<List<Movie>>

    suspend fun saveRecommendedMovies(movies: List<Movie>): Result<Unit>
}
