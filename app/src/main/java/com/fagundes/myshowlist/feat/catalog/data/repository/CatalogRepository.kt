package com.fagundes.myshowlist.feat.catalog.data.repository

import com.fagundes.myshowlist.core.domain.Movie

interface CatalogRepository {
    suspend fun getMoviesByCategory(category: Int): Result<List<Movie>>

    suspend fun searchMoviesByName(query: String): Result<List<Movie>>

    suspend fun getUpcomingMovies(): Result<List<Movie>>
}
