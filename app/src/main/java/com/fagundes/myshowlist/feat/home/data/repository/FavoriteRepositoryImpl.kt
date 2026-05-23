package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.domain.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val favoriteDao: FavoriteDao,
) : FavoriteRepository {
    override fun observeAllFavorites(): Flow<List<Movie>> {
        return favoriteDao.observeAllFavorites().map { entities ->
            entities.map { entity ->
                Movie(
                    id = entity.id,
                    title = entity.title,
                    posterUrl = entity.posterUrl,
                    overview = entity.overview,
                    rating = entity.rating,
                )
            }
        }
    }
}
