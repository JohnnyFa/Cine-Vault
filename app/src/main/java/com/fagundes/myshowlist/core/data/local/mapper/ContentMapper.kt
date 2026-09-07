package com.fagundes.myshowlist.core.data.local.mapper

import com.fagundes.myshowlist.core.data.local.entity.ContentEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.Movie

fun ContentEntity.toMovie(): Movie =
    Movie(
        id = id,
        title = title,
        posterUrl = posterUrl,
        overview = overview,
        rating = rating,
    )

fun ContentEntity.toAnime(): Movie =
    Movie(
        id = id,
        title = title,
        posterUrl = posterUrl,
        overview = overview,
        rating = rating,
    )

fun ContentEntity.toTvShow(): Movie =
    Movie(
        id = id.toInt(),
        title = title,
        posterUrl = posterUrl,
        overview = overview,
        rating = rating,
    )

fun Movie.toEntity(
    contentType: ContentType,
    category: ContentCategory,
): ContentEntity =
    ContentEntity(
        id = id,
        type = contentType,
        title = title,
        posterUrl = posterUrl,
        backdropPath = null,
        overview = overview,
        rating = rating,
        releaseDate = null,
        category = category,
        cachedAt = System.currentTimeMillis(),
    )
