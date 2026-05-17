package com.fagundes.myshowlist.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.fagundes.myshowlist.core.data.local.entity.ContentEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory

@Dao
interface ContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contents: List<ContentEntity>)

    @Query("SELECT * FROM content WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ContentEntity)

    @Query("SELECT * FROM content WHERE category = :category ORDER BY cachedAt DESC")
    fun observeCategory(category: ContentCategory): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE category = :category ORDER BY cachedAt DESC")
    suspend fun getCategory(category: ContentCategory): List<ContentEntity>

    @Query("DELETE FROM content WHERE category = :category")
    suspend fun replaceCategory(category: ContentCategory)

    @Query("DELETE FROM content WHERE cachedAt < :olderThan")
    suspend fun deleteExpiredCache(olderThan: Long)
}
