package com.fagundes.myshowlist.feat.login.presentation.login

import androidx.annotation.StringRes

sealed interface LoginUiState {
    object Idle : LoginUiState

    object Loading : LoginUiState

    data class Error(
        @StringRes val message: Int,
    ) : LoginUiState
}

sealed interface LoginUiEvent {
    object NavigateHome : LoginUiEvent
}
