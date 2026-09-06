package com.fagundes.myshowlist.feat.catalog.data.remote

import com.fagundes.myshowlist.core.data.mapper.toDomain
import com.fagundes.myshowlist.core.data.remote.api.MovieApi
import com.fagundes.myshowlist.core.domain.Movie

class CatalogRemoteDataSourceImpl(
    private val movieApi: MovieApi,
) : CatalogRemoteDataSource {
    override suspend fun getMoviesByGenre(genreId: Int): List<Movie> =
        movieApi.getMoviesByCategory(genreId)
            .results
            .map { it.toDomain() }

    override suspend fun searchMoviesByName(query: String): List<Movie> =
        movieApi.getMoviesByName(query)
            .results
            .map { it.toDomain() }

    override suspend fun getUpcomingMovies(): List<Movie> =
        movieApi.getUpcomingMovies()
            .results
            .map { it.toDomain() }
}
