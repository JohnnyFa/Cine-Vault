package com.fagundes.myshowlist.feat.login.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.feat.login.domain.usecase.ContinueAsGuestUseCase
import com.fagundes.myshowlist.feat.login.domain.usecase.LoginWithGoogleUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginWithGoogle: LoginWithGoogleUseCase,
    private val continueAsGuest: ContinueAsGuestUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent =
        MutableSharedFlow<LoginUiEvent>(
            replay = 0,
            extraBufferCapacity = 1,
        )
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    fun onGoogleTokenReceived(idToken: String) {
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            loginWithGoogle(idToken)
                .onSuccess {
                    _uiState.value = LoginUiState.Idle
                    _uiEvent.tryEmit(LoginUiEvent.NavigateHome)
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(error.message ?: "Erro Firebase")
                }
        }
    }

    fun onContinueAsGuest() {
        // Firebase can't cancel a pending Google sign-in; letting a guest in now would sign them in behind their back.
        if (_uiState.value is LoginUiState.Loading) return
        continueAsGuest()
        _uiEvent.tryEmit(LoginUiEvent.NavigateHome)
    }
}
