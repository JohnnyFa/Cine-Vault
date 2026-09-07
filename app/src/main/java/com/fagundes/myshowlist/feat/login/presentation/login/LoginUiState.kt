package com.fagundes.myshowlist.feat.login.presentation.login

sealed interface LoginUiState {
    object Idle : LoginUiState

    object Loading : LoginUiState

    data class Error(
        val message: String,
    ) : LoginUiState
}

sealed interface LoginUiEvent {
    object NavigateHome : LoginUiEvent
}
