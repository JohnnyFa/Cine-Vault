package com.fagundes.myshowlist.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fagundes.myshowlist.core.data.local.entity.RecentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(recent: RecentEntity)

    @Query("SELECT * FROM recents ORDER BY viewedAt DESC LIMIT 20")
    fun observeRecents(): Flow<List<RecentEntity>>

    @Query("DELETE FROM recents WHERE rowid NOT IN (SELECT rowid FROM recents ORDER BY viewedAt DESC LIMIT 20)")
    suspend fun deleteOldRecents()
}
