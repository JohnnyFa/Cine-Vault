package com.fagundes.myshowlist.feat.home.presentation.home

sealed interface HomeUiState<out T> {
    object Idle : HomeUiState<Nothing>

    object Loading : HomeUiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : HomeUiState<T>

    data class Error(
        val message: String,
    ) : HomeUiState<Nothing>
}
