package com.fagundes.myshowlist.feat.catalog.presentation.upcoming

import com.fagundes.myshowlist.core.domain.Movie

sealed interface UpcomingUiState {
    object Loading : UpcomingUiState

    data class Content(
        val movies: List<Movie>,
    ) : UpcomingUiState

    data class Error(
        val message: String,
    ) : UpcomingUiState
}
