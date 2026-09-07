package com.fagundes.myshowlist.core.data.local.datasource

import com.fagundes.myshowlist.core.data.local.dao.RecentDao
import com.fagundes.myshowlist.core.data.local.entity.RecentEntity
import kotlinx.coroutines.flow.Flow

class RecentLocalDataSourceImpl(
    private val dao: RecentDao,
) : RecentLocalDataSource {
    override fun observeRecents(): Flow<List<RecentEntity>> = dao.observeRecents()

    override fun observeCount(): Flow<Int> = dao.observeCount()

    override suspend fun save(recent: RecentEntity) {
        dao.upsert(recent)
    }

    override suspend fun trimToLimit() {
        dao.deleteOldRecents()
    }

    override suspend fun clearAll() {
        dao.deleteAll()
    }
}
