package com.fagundes.myshowlist.feat.options.presentation.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearCacheUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearFavoritesUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearRecentsUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearUserDataUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.GetCurrentUserUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveFavoritesCountUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveRecentsCountUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val signOut: SignOutUseCase,
    private val clearUserData: ClearUserDataUseCase,
    private val observeFavoritesCount: ObserveFavoritesCountUseCase,
    private val observeRecentsCount: ObserveRecentsCountUseCase,
    private val clearFavorites: ClearFavoritesUseCase,
    private val clearRecents: ClearRecentsUseCase,
    private val clearCache: ClearCacheUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OptionsUiState(user = getCurrentUser()))
    val uiState: StateFlow<OptionsUiState> = _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<OptionsEvent>(
            replay = 0,
            extraBufferCapacity = 1,
        )
    val events: SharedFlow<OptionsEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeFavoritesCount().collect { count ->
                _uiState.update { it.copy(favoritesCount = count) }
            }
        }
        viewModelScope.launch {
            observeRecentsCount().collect { count ->
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
                    ClearAction.Favorites -> clearFavorites()
                    ClearAction.Recents -> clearRecents()
                    ClearAction.Cache -> clearCache()
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Sign-out proceeds even if the local wipe fails, so the user is never stuck signed in.
            runCatching { clearUserData() }
            signOut()
            _events.tryEmit(OptionsEvent.LoggedOut)
        }
    }
}
