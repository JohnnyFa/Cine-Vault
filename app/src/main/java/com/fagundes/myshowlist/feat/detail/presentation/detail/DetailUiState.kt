package com.fagundes.myshowlist.feat.detail.presentation.detail

import androidx.annotation.StringRes
import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val ui: ContentDetail,
        val isFavorite: Boolean = false,
        val isFavoriteLoading: Boolean = false,
    ) : DetailUiState

    data class Error(
        @StringRes val message: Int,
    ) : DetailUiState
}

sealed interface DetailEvent {
    data class FavoriteUpdated(
        val isFavorite: Boolean,
    ) : DetailEvent

    data object RequireSignIn : DetailEvent

    data class ShowError(
        @StringRes val message: Int,
    ) : DetailEvent
}
