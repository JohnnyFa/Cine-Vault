package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.CACHE_DURATION
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.data.local.mapper.toEntity
import com.fagundes.myshowlist.core.data.local.mapper.toMovie
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.data.local.datasource.HomeLocalDataSource
import com.fagundes.myshowlist.feat.home.data.remote.HomeRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl(
    private val local: HomeLocalDataSource,
    private val remote: HomeRemoteDataSource
): HomeRepository {
    override fun observePopularMovies(): Flow<List<Movie>> = local.observeMoviesByCategory(ContentCategory.POPULAR)
        .map { list -> list.map { it.toMovie() } }
        .distinctUntilChanged()

    override fun observeRecommendedMovies(): Flow<List<Movie>> = local.observeMoviesByCategory(ContentCategory.RECOMMENDED)
        .map { list -> list.map { it.toMovie() } }
        .distinctUntilChanged()

    override fun observeShowOfTheDay(): Flow<Movie?> = local.observeMoviesByCategory(ContentCategory.SHOW_OF_THE_DAY)
        .map { it.firstOrNull()?.toMovie() }
        .distinctUntilChanged()

    override suspend fun refreshHomeIfNeeded() {
        val now = System.currentTimeMillis()
        val minValid = now - CACHE_DURATION
        local.clearExpired(minValid)

        refreshCategoryIfNeeded(ContentCategory.POPULAR, minValid) { remote.getPopularMovies() }
        refreshCategoryIfNeeded(ContentCategory.RECOMMENDED, minValid) { remote.getRecommendedMovies() }
        refreshCategoryIfNeeded(ContentCategory.SHOW_OF_THE_DAY, minValid) { listOf(remote.getShowOfTheDay()) }
    }

    private suspend fun refreshCategoryIfNeeded(
        category: ContentCategory,
        minValid: Long,
        fetch: suspend () -> List<Movie>
    ) {
        val cached = local.getMoviesByCategory(category)
        val expired = cached.isEmpty() || cached.any { it.cachedAt < minValid }
        if (!expired) return

        val remoteItems = fetch()
        local.saveMoviesForCategory(
            category,
            remoteItems.map { it.toEntity(ContentType.MOVIE, category) }
        )
    }
}
