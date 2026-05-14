package com.fagundes.myshowlist.feat.detail.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.detail.data.repository.DetailRepository
import com.fagundes.myshowlist.feat.detail.domain.ContentDetailUi
import com.fagundes.myshowlist.feat.detail.domain.FavoriteItem
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveFavoriteStateUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.ToggleFavoriteUseCase
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
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DetailEvent>()
    val events: SharedFlow<DetailEvent> = _events.asSharedFlow()
    private val latestFavoriteState = MutableStateFlow(false)

    init {
        observeFavoriteState()
        loadDetail()
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

    private fun loadDetail() {
        viewModelScope.launch {
            repository.getContentDetail(id.toString(), type.name)
                .onSuccess {
                    repository.cacheFavoriteCandidate(
                        FavoriteItem(
                            id = it.id,
                            type = type,
                            title = it.title,
                            posterUrl = it.imageUrl,
                            overview = it.overview,
                            rating = it.rating
                        )
                    )
                    _uiState.value = DetailUiState.Success(
                        ui = it,
                        isFavorite = latestFavoriteState.value
                    )
                }
                .onFailure {
                    _uiState.value = DetailUiState.Error("Failed to load content")
                }
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(
        val ui: ContentDetailUi,
        val isFavorite: Boolean = false,
        val isFavoriteLoading: Boolean = false
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

sealed interface DetailEvent {
    data class FavoriteUpdated(val isFavorite: Boolean) : DetailEvent
    data class ShowError(val message: String) : DetailEvent
}
