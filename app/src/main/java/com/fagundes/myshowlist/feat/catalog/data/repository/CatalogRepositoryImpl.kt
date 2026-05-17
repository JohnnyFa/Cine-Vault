package com.fagundes.myshowlist.feat.catalog.data.repository

import com.fagundes.myshowlist.core.CACHE_DURATION
import com.fagundes.myshowlist.core.data.mapper.toDomain
import com.fagundes.myshowlist.core.data.local.mapper.toEntity
import com.fagundes.myshowlist.core.data.local.mapper.toMovie
import com.fagundes.myshowlist.core.data.remote.api.MovieApi
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.home.data.local.datasource.HomeLocalDataSource

class CatalogRepositoryImpl(
    private val movieApi: MovieApi,
    private val local: HomeLocalDataSource
) : CatalogRepository {

    override suspend fun getMoviesByCategory(category: Int): Result<List<Movie>> = runCatching {
        movieApi.getMoviesByCategory(category)
            .results
            .map { it.toDomain() }
    }

    override suspend fun searchMoviesByName(query: String): Result<List<Movie>> = runCatching {
        movieApi.getMoviesByName(query)
            .results
            .map { it.toDomain() }
    }

    override suspend fun getUpcomingMovies(): Result<List<Movie>> = runCatching {
        val minValidTime = System.currentTimeMillis() - CACHE_DURATION

        val cached = local.getMoviesByCategory(category = ContentCategory.UPCOMING)

        if (cached.isNotEmpty() && cached.all { it.cachedAt >= minValidTime }) {
            return@runCatching cached.map { it.toMovie() }
        }

        val remoteMovies = movieApi.getUpcomingMovies()
            .results
            .map { it.toDomain() }

        local.saveMoviesForCategory(
            category = ContentCategory.UPCOMING,
            items = remoteMovies.map {
                it.toEntity(
                    contentType = ContentType.MOVIE,
                    category = ContentCategory.UPCOMING
                )
            }
        )

        remoteMovies
    }
}