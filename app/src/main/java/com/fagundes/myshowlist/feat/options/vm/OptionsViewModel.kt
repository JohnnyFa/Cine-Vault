package com.fagundes.myshowlist.feat.options.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearCacheUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearFavoritesUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearRecentsUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearUserDataUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveFavoritesCountUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveRecentsCountUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OptionsUiState(
    val favoritesCount: Int = 0,
    val recentsCount: Int = 0,
    val pendingClearAction: ClearAction? = null,
)

sealed interface ClearAction {
    data object Favorites : ClearAction
    data object Recents : ClearAction
    data object Cache : ClearAction
}

class OptionsViewModel(
    private val auth: FirebaseAuth,
    private val clearUserDataUseCase: ClearUserDataUseCase,
    private val observeFavoritesCountUseCase: ObserveFavoritesCountUseCase,
    private val observeRecentsCountUseCase: ObserveRecentsCountUseCase,
    private val clearFavoritesUseCase: ClearFavoritesUseCase,
    private val clearRecentsUseCase: ClearRecentsUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
) : ViewModel() {

    val currentUser: FirebaseUser? = auth.currentUser

    private val _uiState = MutableStateFlow(OptionsUiState())
    val uiState: StateFlow<OptionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeFavoritesCountUseCase().collect { count ->
                _uiState.update { it.copy(favoritesCount = count) }
            }
        }
        viewModelScope.launch {
            observeRecentsCountUseCase().collect { count ->
                _uiState.update { it.copy(recentsCount = count) }
            }
        }
    }

    fun requestClear(action: ClearAction) {
        _uiState.update { it.copy(pendingClearAction = action) }
    }

    fun dismissClearDialog() {
        _uiState.update { it.copy(pendingClearAction = null) }
    }

    fun confirmClear() {
        val action = _uiState.value.pendingClearAction ?: return
        _uiState.update { it.copy(pendingClearAction = null) }
        viewModelScope.launch {
            runCatching {
                when (action) {
                    ClearAction.Favorites -> clearFavoritesUseCase()
                    ClearAction.Recents -> clearRecentsUseCase()
                    ClearAction.Cache -> clearCacheUseCase()
                }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            runCatching { clearUserDataUseCase() }
            auth.signOut()
            onComplete()
        }
    }
}
