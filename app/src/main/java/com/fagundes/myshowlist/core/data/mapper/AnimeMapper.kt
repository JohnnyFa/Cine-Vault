package com.fagundes.myshowlist.core.data.mapper

import com.fagundes.myshowlist.core.data.remote.dto.AnimeDto
import com.fagundes.myshowlist.core.domain.Anime

fun AnimeDto.toDomain(): Anime =
    Anime(
        id = malId,
        title = title,
        imageUrl = images.jpg.imageUrl,
        overview = "",
        rating = 0.0,
    )
