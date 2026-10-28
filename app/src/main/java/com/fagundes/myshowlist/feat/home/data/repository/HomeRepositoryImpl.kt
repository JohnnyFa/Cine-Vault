package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.CACHE_DURATION
import com.fagundes.myshowlist.core.data.local.datasource.ContentLocalDataSource
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.data.local.mapper.toEntity
import com.fagundes.myshowlist.core.data.local.mapper.toMovie
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.data.remote.HomeRemoteDataSource
import com.fagundes.myshowlist.feat.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

class HomeRepositoryImpl(
    private val local: ContentLocalDataSource,
    private val remote: HomeRemoteDataSource,
) : HomeRepository {
    private val recommendationsBySeed = ConcurrentHashMap<Int, List<Movie>>()

    override fun observePopularMovies(): Flow<List<Movie>> =
        local.observeMoviesByCategory(ContentCategory.POPULAR)
            .map { list -> list.map { it.toMovie() } }
            .distinctUntilChanged()

    override fun observeRecommendedMovies(): Flow<List<Movie>> =
        local.observeMoviesByCategory(ContentCategory.RECOMMENDED)
            .map { list -> list.map { it.toMovie() } }
            .distinctUntilChanged()

    override fun observeShowOfTheDay(): Flow<Movie?> =
        local.observeMoviesByCategory(ContentCategory.SHOW_OF_THE_DAY)
            .map { it.firstOrNull()?.toMovie() }
            .distinctUntilChanged()

    override suspend fun refreshHomeIfNeeded(): Result<Unit> =
        runCatching {
            val now = System.currentTimeMillis()
            val minValid = now - CACHE_DURATION
            local.clearExpired(minValid)

            refreshCategoryIfNeeded(ContentCategory.POPULAR, minValid) { remote.getPopularMovies() }
            refreshCategoryIfNeeded(ContentCategory.SHOW_OF_THE_DAY, minValid) { listOf(remote.getShowOfTheDay()) }
        }

    // TMDB's suggestions for a movie barely change, so each seed is fetched once per process.
    override suspend fun getRecommendationsFor(movieId: Int): Result<List<Movie>> =
        runCatching {
            recommendationsBySeed[movieId] ?: remote.getRecommendationsFor(movieId).also { recommendationsBySeed[movieId] = it }
        }

    override suspend fun getTopRatedMovies(): Result<List<Movie>> = runCatching { remote.getTopRatedMovies() }

    override suspend fun saveRecommendedMovies(movies: List<Movie>): Result<Unit> =
        runCatching {
            // The category is read back ORDER BY cachedAt DESC, and a batch mapped in the same
            // millisecond would tie, so each rank gets its own timestamp to keep the order.
            val now = System.currentTimeMillis()
            local.saveMoviesForCategory(
                ContentCategory.RECOMMENDED,
                movies.mapIndexed { rank, movie ->
                    movie.toEntity(ContentType.MOVIE, ContentCategory.RECOMMENDED).copy(cachedAt = now - rank)
                },
            )
        }

    private suspend fun refreshCategoryIfNeeded(
        category: ContentCategory,
        minValid: Long,
        fetch: suspend () -> List<Movie>,
    ) {
        val cached = local.getMoviesByCategory(category)
        val expired = cached.isEmpty() || cached.any { it.cachedAt < minValid }
        if (!expired) return

        val remoteItems = fetch()
        local.saveMoviesForCategory(
            category,
            remoteItems.map { it.toEntity(ContentType.MOVIE, category) },
        )
    }
}
