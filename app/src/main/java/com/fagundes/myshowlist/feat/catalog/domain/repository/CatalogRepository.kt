package com.fagundes.myshowlist.feat.catalog.domain.repository

import com.fagundes.myshowlist.core.domain.Movie

interface CatalogRepository {
    suspend fun getMoviesByGenre(genreId: Int): Result<List<Movie>>

    suspend fun searchMoviesByName(query: String): Result<List<Movie>>

    suspend fun getUpcomingMovies(): Result<List<Movie>>
}
