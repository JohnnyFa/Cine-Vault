package com.fagundes.myshowlist.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fagundes.myshowlist.core.data.local.entity.CachedMovieDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDetailCacheDao {
    @Query("SELECT * FROM movie_detail_cache WHERE id = :id LIMIT 1")
    fun observeMovieById(id: Int): Flow<CachedMovieDetailEntity?>

    @Query("SELECT * FROM movie_detail_cache WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: Int): CachedMovieDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(movie: CachedMovieDetailEntity)

    @Query("DELETE FROM movie_detail_cache WHERE cachedAt < :olderThan")
    suspend fun deleteExpiredCache(olderThan: Long)

    @Query("DELETE FROM movie_detail_cache")
    suspend fun deleteAll()
}
