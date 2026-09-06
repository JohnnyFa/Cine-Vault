package com.fagundes.myshowlist.feat.catalog.data.remote

import com.fagundes.myshowlist.core.domain.Movie

interface CatalogRemoteDataSource {
    suspend fun getMoviesByGenre(genreId: Int): List<Movie>

    suspend fun searchMoviesByName(query: String): List<Movie>

    suspend fun getUpcomingMovies(): List<Movie>
}
