package com.fagundes.myshowlist.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("vote_average")
    val rating: Double?,
    val overview: String?,
    @SerialName("release_date")
    val releaseDate: String? = null,
    val runtime: Int? = null,
    val genres: List<GenreDto>? = null
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)
