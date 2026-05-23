package com.fagundes.myshowlist.feat.detail.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.detail.data.repository.DetailRepository
import com.fagundes.myshowlist.feat.detail.domain.ContentDetailUi
import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveFavoriteStateUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.ToggleFavoriteUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.SaveRecentMovieUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val id: Int,
    private val type: ContentType,
    private val repository: DetailRepository,
    private val observeFavoriteStateUseCase: ObserveFavoriteStateUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val saveRecentMovieUseCase: SaveRecentMovieUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DetailEvent>()
    val events: SharedFlow<DetailEvent> = _events.asSharedFlow()
    private val latestFavoriteState = MutableStateFlow(false)

    init {
        observeFavoriteState()
        observeDetail()
        refreshDetail()
    }

    fun onFavoriteClick() {
        val current = _uiState.value
        if (current !is DetailUiState.Success || current.isFavoriteLoading) return

        viewModelScope.launch {
            _uiState.update { state ->
                (state as? DetailUiState.Success)?.copy(isFavoriteLoading = true) ?: state
            }

            toggleFavoriteUseCase(id, type)
                .onSuccess { isFavorite ->
                    _uiState.update { state ->
                        (state as? DetailUiState.Success)?.copy(isFavoriteLoading = false, isFavorite = isFavorite)
                            ?: state
                    }
                    _events.emit(DetailEvent.FavoriteUpdated(isFavorite))
                }
                .onFailure {
                    _uiState.update { state ->
                        (state as? DetailUiState.Success)?.copy(isFavoriteLoading = false) ?: state
                    }
                    _events.emit(DetailEvent.ShowError("Não foi possível atualizar favorito"))
                }
        }
    }

    private fun observeFavoriteState() {
        viewModelScope.launch {
            observeFavoriteStateUseCase(id, type).collect { isFavorite ->
                latestFavoriteState.value = isFavorite
                _uiState.update { state ->
                    (state as? DetailUiState.Success)?.copy(isFavorite = isFavorite) ?: state
                }
            }
        }
    }

    private fun observeDetail() {
        viewModelScope.launch {
            repository.observeContentDetail(id, type.name).collect { detail ->
                if (detail != null) {
                    val favoriteItem =
                        FavoriteItem(
                            id = detail.id,
                            type = type,
                            title = detail.title,
                            posterUrl = detail.imageUrl,
                            overview = detail.overview,
                            rating = detail.rating,
                        )
                    repository.cacheFavoriteCandidate(favoriteItem)
                    saveRecentMovieUseCase(favoriteItem)
                    _uiState.value = DetailUiState.Success(ui = detail, isFavorite = latestFavoriteState.value)
                }
            }
        }
    }

    private fun refreshDetail() {
        viewModelScope.launch {
            runCatching { repository.refreshDetailIfNeeded(id) }
                .onFailure {
                    if (_uiState.value is DetailUiState.Loading) {
                        _uiState.value = DetailUiState.Error("Failed to load content")
                    }
                }
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val ui: ContentDetailUi,
        val isFavorite: Boolean = false,
        val isFavoriteLoading: Boolean = false,
    ) : DetailUiState

    data class Error(val message: String) : DetailUiState
}

sealed interface DetailEvent {
    data class FavoriteUpdated(val isFavorite: Boolean) : DetailEvent

    data class ShowError(val message: String) : DetailEvent
}
