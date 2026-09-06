package com.fagundes.myshowlist.feat.catalog.data.repository

import com.fagundes.myshowlist.core.CACHE_DURATION
import com.fagundes.myshowlist.core.data.local.datasource.ContentLocalDataSource
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.data.local.mapper.toEntity
import com.fagundes.myshowlist.core.data.local.mapper.toMovie
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.data.remote.CatalogRemoteDataSource
import com.fagundes.myshowlist.feat.catalog.domain.repository.CatalogRepository

class CatalogRepositoryImpl(
    private val remote: CatalogRemoteDataSource,
    private val local: ContentLocalDataSource,
) : CatalogRepository {
    override suspend fun getMoviesByGenre(genreId: Int): Result<List<Movie>> =
        runCatching {
            remote.getMoviesByGenre(genreId)
        }

    override suspend fun searchMoviesByName(query: String): Result<List<Movie>> =
        runCatching {
            remote.searchMoviesByName(query)
        }

    override suspend fun getUpcomingMovies(): Result<List<Movie>> =
        runCatching {
            val minValidTime = System.currentTimeMillis() - CACHE_DURATION

            val cached = local.getMoviesByCategory(category = ContentCategory.UPCOMING)

            if (cached.isNotEmpty() && cached.all { it.cachedAt >= minValidTime }) {
                return@runCatching cached.map { it.toMovie() }
            }

            val remoteMovies = remote.getUpcomingMovies()

            local.saveMoviesForCategory(
                category = ContentCategory.UPCOMING,
                items =
                    remoteMovies.map {
                        it.toEntity(
                            contentType = ContentType.MOVIE,
                            category = ContentCategory.UPCOMING,
                        )
                    },
            )

            remoteMovies
        }
}
