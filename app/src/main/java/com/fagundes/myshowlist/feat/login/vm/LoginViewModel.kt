package com.fagundes.myshowlist.feat.login.vm

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(
    private val auth: FirebaseAuth,
) : ViewModel() {
    // ---------- STATE ----------
    private val _uiState =
        MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    // ---------- EVENT ----------
    private val _uiEvent =
        MutableSharedFlow<LoginUiEvent>(
            replay = 0,
            extraBufferCapacity = 1,
        )
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent

    fun onGoogleTokenReceived(idToken: String) {
        _uiState.value = LoginUiState.Loading

        val credential =
            GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                _uiState.value = LoginUiState.Idle
                _uiEvent.tryEmit(LoginUiEvent.NavigateHome)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                _uiState.value = LoginUiState.Error(e.message ?: "Erro Firebase")
            }
    }
}
