package com.fagundes.myshowlist.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fagundes.myshowlist.core.data.local.dao.ContentDao
import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.data.local.dao.RecentDao
import com.fagundes.myshowlist.core.data.local.entity.ContentEntity
import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import com.fagundes.myshowlist.core.data.local.entity.RecentEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentTypeConverter

@Database(
    entities = [ContentEntity::class, FavoriteEntity::class, RecentEntity::class],
    version = 3,
)
@TypeConverters(ContentTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao
}
