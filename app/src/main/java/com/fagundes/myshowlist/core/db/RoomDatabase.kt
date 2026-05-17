package com.fagundes.myshowlist.core.db

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.TypeConverters
import com.fagundes.myshowlist.core.data.local.dao.ContentDao
import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.data.local.dao.RecentDao
import com.fagundes.myshowlist.core.data.local.dao.MovieDetailCacheDao
import com.fagundes.myshowlist.core.data.local.entity.ContentEntity
import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import com.fagundes.myshowlist.core.data.local.entity.RecentEntity
import com.fagundes.myshowlist.core.data.local.entity.CachedMovieDetailEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentTypeConverter

@Database(
    entities = [ContentEntity::class, FavoriteEntity::class, RecentEntity::class, CachedMovieDetailEntity::class],
    version = 5,
)
@TypeConverters(ContentTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao
    abstract fun movieDetailCacheDao(): MovieDetailCacheDao
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


val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `content_new` (
                `id` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `posterUrl` TEXT,
                `backdropPath` TEXT,
                `overview` TEXT,
                `rating` REAL,
                `releaseDate` TEXT,
                `category` TEXT NOT NULL,
                `cachedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`, `category`)
            )
        """.trimIndent())
        db.execSQL("""
            INSERT OR REPLACE INTO `content_new` (`id`,`type`,`title`,`posterUrl`,`backdropPath`,`overview`,`rating`,`releaseDate`,`category`,`cachedAt`)
            SELECT `id`,`type`,`title`,`posterUrl`,NULL,`overview`,`rating`,NULL,`category`,`lastUpdated` FROM `content`
        """.trimIndent())
        db.execSQL("DROP TABLE `content`")
        db.execSQL("ALTER TABLE `content_new` RENAME TO `content`")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_content_category` ON `content` (`category`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_content_id` ON `content` (`id`)")
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `movie_detail_cache` (
                `id` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `overview` TEXT,
                `backdropPath` TEXT,
                `posterPath` TEXT,
                `voteAverage` REAL,
                `genres` TEXT,
                `runtime` INTEGER,
                `releaseDate` TEXT,
                `cachedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """.trimIndent())
    }
}
