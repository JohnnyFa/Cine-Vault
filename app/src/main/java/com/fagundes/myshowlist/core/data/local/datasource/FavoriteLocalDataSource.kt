package com.fagundes.myshowlist.core.data.local.datasource

import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {
    fun observeAll(): Flow<List<FavoriteEntity>>

    fun observeCount(): Flow<Int>

    suspend fun clearAll()
}
