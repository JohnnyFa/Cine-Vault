package com.fagundes.myshowlist.core.db

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 4,
)
@TypeConverters(ContentTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao
}


val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `recents_new` (
                `id` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `posterUrl` TEXT,
                `rating` REAL,
                `viewedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`, `type`)
            )
        """.trimIndent())
        db.execSQL("""
            INSERT OR REPLACE INTO `recents_new` (`id`, `type`, `title`, `posterUrl`, `rating`, `viewedAt`)
            SELECT `id`, `type`, `title`, `posterUrl`, `rating`, `viewedAt` FROM `recents`
        """.trimIndent())
        db.execSQL("DROP TABLE `recents`")
        db.execSQL("ALTER TABLE `recents_new` RENAME TO `recents`")
    }
}
