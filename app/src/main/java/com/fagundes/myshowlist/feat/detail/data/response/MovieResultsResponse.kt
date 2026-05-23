package com.fagundes.myshowlist.feat.detail.data.response

import com.fagundes.myshowlist.core.data.remote.dto.MovieDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResultsResponse(
    @SerialName("movie_results")
    val movieResults: List<MovieDto>,
)
