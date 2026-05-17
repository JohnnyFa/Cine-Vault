package com.fagundes.myshowlist.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_detail_cache")
data class CachedMovieDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String?,
    val backdropPath: String?,
    val posterPath: String?,
    val voteAverage: Double?,
    val genres: String?,
    val runtime: Int?,
    val releaseDate: String?,
    val cachedAt: Long
)
