package com.fagundes.myshowlist.feat.detail.data.repository

import com.fagundes.myshowlist.core.CACHE_DURATION
import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.data.local.dao.MovieDetailCacheDao
import com.fagundes.myshowlist.core.data.local.entity.CachedMovieDetailEntity
import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.data.remote.api.MovieApi
import com.fagundes.myshowlist.feat.detail.domain.ContentDetailUi
import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DetailRepositoryImpl(
    private val movieApi: MovieApi,
    private val favoriteDao: FavoriteDao,
    private val detailCacheDao: MovieDetailCacheDao
) : DetailRepository {

    private val favoriteCandidates = mutableMapOf<Int, FavoriteItem>()

    override fun observeContentDetail(id: Int, type: String): Flow<ContentDetailUi?> =
        detailCacheDao.observeMovieById(id).map { cached ->
            cached?.let {
                ContentDetailUi(
                    id = it.id,
                    title = it.title,
                    imageUrl = it.posterPath,
                    overview = it.overview,
                    rating = it.voteAverage,
                    type = type
                )
            }
        }.distinctUntilChanged()

    override suspend fun refreshDetailIfNeeded(id: Int) {
        val now = System.currentTimeMillis()
        val cached = detailCacheDao.getMovieById(id)
        val expired = cached == null || (now - cached.cachedAt) > CACHE_DURATION
        if (!expired) return

        val remote = movieApi.getContentById(id)
        detailCacheDao.upsert(
            CachedMovieDetailEntity(
                id = remote.id,
                title = remote.title,
                overview = remote.overview,
                backdropPath = remote.backdropPath,
                posterPath = remote.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                voteAverage = remote.rating,
                genres = remote.genres?.joinToString(",") { it.name },
                runtime = remote.runtime,
                releaseDate = remote.releaseDate,
                cachedAt = now
            )
        )
        detailCacheDao.deleteExpiredCache(now - CACHE_DURATION)
    }

    override suspend fun cacheFavoriteCandidate(item: FavoriteItem) { favoriteCandidates[item.id] = item }
    override fun observeFavoriteState(id: Int, type: ContentType): Flow<Boolean> = favoriteDao.observeById(id, type).map { it != null }
    override suspend fun toggleFavorite(id: Int, type: ContentType): Result<Boolean> = runCatching { if (favoriteDao.isFavorite(id, type)) { favoriteDao.remove(id, type); false } else { val c=checkNotNull(favoriteCandidates[id]); favoriteDao.upsert(FavoriteEntity(c.id,c.type,c.title,c.posterUrl,c.overview,c.rating,System.currentTimeMillis())); true } }
}
