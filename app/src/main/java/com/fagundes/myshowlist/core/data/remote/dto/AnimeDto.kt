package com.fagundes.myshowlist.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeDto(
    @SerialName("mal_id")
    val malId: Int,
    val title: String,
    val images: ImagesDto,
)

@Serializable
data class ImagesDto(
    val jpg: JpgDto,
)

@Serializable
data class JpgDto(
    @SerialName("image_url")
    val imageUrl: String?,
)
