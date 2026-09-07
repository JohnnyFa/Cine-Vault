package com.fagundes.myshowlist.core.data.local.datasource

import com.fagundes.myshowlist.core.data.local.entity.RecentEntity
import kotlinx.coroutines.flow.Flow

interface RecentLocalDataSource {
    fun observeRecents(): Flow<List<RecentEntity>>

    fun observeCount(): Flow<Int>

    suspend fun save(recent: RecentEntity)

    suspend fun trimToLimit()

    suspend fun clearAll()
}
