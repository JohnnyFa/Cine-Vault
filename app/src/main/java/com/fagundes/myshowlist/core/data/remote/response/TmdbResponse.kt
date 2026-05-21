package com.fagundes.myshowlist.core.data.remote.response

import com.fagundes.myshowlist.core.data.remote.dto.MovieDto
import kotlinx.serialization.Serializable

@Serializable
data class TmdbResponse(
    val page: Int,
    val results: List<MovieDto>,
)
