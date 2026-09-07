package com.fagundes.myshowlist.core.data.mapper

import com.fagundes.myshowlist.core.data.remote.dto.MovieDto
import com.fagundes.myshowlist.core.domain.Movie

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
