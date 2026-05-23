package com.fagundes.myshowlist.feat.login.vm

sealed interface LoginUiState {
    object Idle : LoginUiState

    object Loading : LoginUiState

    data class Error(val message: String) : LoginUiState
}
