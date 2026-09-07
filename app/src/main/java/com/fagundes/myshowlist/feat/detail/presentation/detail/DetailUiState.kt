package com.fagundes.myshowlist.feat.detail.presentation.detail

import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val ui: ContentDetail,
        val isFavorite: Boolean = false,
        val isFavoriteLoading: Boolean = false,
    ) : DetailUiState

    data class Error(
        val message: String,
    ) : DetailUiState
}

sealed interface DetailEvent {
    data class FavoriteUpdated(
        val isFavorite: Boolean,
    ) : DetailEvent

    data class ShowError(
        val message: String,
    ) : DetailEvent
}
