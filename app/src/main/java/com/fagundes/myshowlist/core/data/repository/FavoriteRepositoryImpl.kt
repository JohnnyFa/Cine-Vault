package com.fagundes.myshowlist.core.data.repository

import com.fagundes.myshowlist.core.data.local.datasource.FavoriteLocalDataSource
import com.fagundes.myshowlist.core.data.local.mapper.toMovie
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val local: FavoriteLocalDataSource,
) : FavoriteRepository {
    override fun observeAllFavorites(): Flow<List<Movie>> = local.observeAll().map { entities -> entities.map { it.toMovie() } }

    override fun observeCount(): Flow<Int> = local.observeCount()

    override suspend fun clearAll() {
        local.clearAll()
    }
}
