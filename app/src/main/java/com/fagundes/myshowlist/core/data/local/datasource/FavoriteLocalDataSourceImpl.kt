package com.fagundes.myshowlist.core.data.local.datasource

import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoriteLocalDataSourceImpl(
    private val dao: FavoriteDao,
) : FavoriteLocalDataSource {
    override fun observeAll(): Flow<List<FavoriteEntity>> = dao.observeAllFavorites()

    override fun observeCount(): Flow<Int> = dao.observeCount()

    override suspend fun clearAll() {
        dao.deleteAll()
    }
}
