package com.fagundes.myshowlist.feat.detail.data.mapper

import com.fagundes.myshowlist.core.data.local.entity.CachedMovieDetailEntity
import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.data.remote.dto.MovieDto
import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail

fun MovieDto.toCachedEntity(cachedAt: Long): CachedMovieDetailEntity =
    CachedMovieDetailEntity(
        id = id,
        title = title,
        overview = overview,
        backdropPath = backdropPath,
        // w500 matches core/data/mapper/MovieMapper; core.TMDB_IMAGE_BASE is w780 and is used by UI code.
        posterPath = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
        voteAverage = rating,
        genres = genres?.joinToString(",") { it.name },
        runtime = runtime,
        releaseDate = releaseDate,
        cachedAt = cachedAt,
    )

fun CachedMovieDetailEntity.toContentDetail(type: ContentType): ContentDetail =
    ContentDetail(
        id = id,
        title = title,
        imageUrl = posterPath,
        overview = overview,
        rating = voteAverage,
        type = type.name,
    )

fun ContentItem.toFavoriteEntity(favoritedAt: Long): FavoriteEntity =
    FavoriteEntity(
        id = id,
        type = type,
        title = title,
        posterUrl = posterUrl,
        overview = overview,
        rating = rating,
        favoritedAt = favoritedAt,
    )
