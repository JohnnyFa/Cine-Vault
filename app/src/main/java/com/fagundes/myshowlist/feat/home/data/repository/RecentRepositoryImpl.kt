package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.data.local.dao.RecentDao
import com.fagundes.myshowlist.core.data.local.entity.RecentEntity
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentRepositoryImpl(
    private val recentDao: RecentDao,
) : RecentRepository {
    override fun observeRecents(): Flow<List<Movie>> {
        return recentDao.observeRecents().map { entities ->
            entities.map { entity ->
                Movie(
                    id = entity.id,
                    title = entity.title,
                    posterUrl = entity.posterUrl,
                    overview = null,
                    rating = entity.rating,
                    type = entity.type,
                )
            }
        }
    }

    override suspend fun saveRecent(movie: FavoriteItem) {
        recentDao.upsert(
            RecentEntity(
                id = movie.id,
                type = movie.type,
                title = movie.title,
                posterUrl = movie.posterUrl,
                rating = movie.rating,
                viewedAt = System.currentTimeMillis(),
            ),
        )
        recentDao.deleteOldRecents()
    }
}
