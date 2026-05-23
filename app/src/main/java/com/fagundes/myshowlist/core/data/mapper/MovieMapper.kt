package com.fagundes.myshowlist.core.data.mapper

import com.fagundes.myshowlist.core.data.remote.dto.MovieDto
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.detail.domain.ContentDetailUi

fun MovieDto.toDomain(): Movie =
    Movie(
        id = id,
        title = title,
        posterUrl =
            posterPath?.let {
                "https://image.tmdb.org/t/p/w500$it"
            },
        rating = rating,
        overview = overview,
    )

fun Movie.toContentDetailUi(type: String?): ContentDetailUi =
    ContentDetailUi(
        id = id,
        title = title,
        imageUrl = posterUrl,
        overview = overview,
        rating = rating,
        type = type,
    )
