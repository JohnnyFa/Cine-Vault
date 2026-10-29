package com.fagundes.myshowlist.feat.catalog.presentation.upcoming

import androidx.annotation.StringRes
import com.fagundes.myshowlist.core.domain.Movie

sealed interface UpcomingUiState {
    object Loading : UpcomingUiState

    data class Content(
        val movies: List<Movie>,
    ) : UpcomingUiState

    data class Error(
        @StringRes val message: Int,
    ) : UpcomingUiState
}
