package com.fagundes.myshowlist.core.data.remote.response

import com.fagundes.myshowlist.core.data.remote.dto.AnimeDto
import kotlinx.serialization.Serializable

@Serializable
data class JikanResponse(
    val data: List<AnimeDto>,
)
