package com.fagundes.myshowlist.feat.detail.data.repository

import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.data.mapper.toContentDetailUi
import com.fagundes.myshowlist.core.data.mapper.toDomain
import com.fagundes.myshowlist.core.data.remote.api.MovieApi
import com.fagundes.myshowlist.feat.detail.domain.ContentDetailUi
import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DetailRepositoryImpl(
    private val movieApi: MovieApi,
    private val favoriteDao: FavoriteDao
) : DetailRepository {

    private val favoriteCandidates = mutableMapOf<Int, FavoriteItem>()

    override suspend fun getContentDetail(
        id: String,
        type: String
    ): Result<ContentDetailUi> = runCatching {
        movieApi.getContentById(id.toInt())
            .toDomain()
            .toContentDetailUi(type)
    }

    override suspend fun cacheFavoriteCandidate(item: FavoriteItem) {
        favoriteCandidates[item.id] = item
    }

    override fun observeFavoriteState(id: Int, type: ContentType): Flow<Boolean> =
        favoriteDao.observeById(id, type).map { it != null }

    override suspend fun toggleFavorite(id: Int, type: ContentType): Result<Boolean> = runCatching {
        val isFavorite = favoriteDao.isFavorite(id, type)
        if (isFavorite) {
            favoriteDao.remove(id, type)
            false
        } else {
            val candidate = checkNotNull(favoriteCandidates[id]) { "Favorite candidate not found for id=$id" }
            favoriteDao.upsert(
                FavoriteEntity(
                    id = candidate.id,
                    type = candidate.type,
                    title = candidate.title,
                    posterUrl = candidate.posterUrl,
                    overview = candidate.overview,
                    rating = candidate.rating,
                    favoritedAt = System.currentTimeMillis()
                )
            )
            true
        }
    }
}
