package com.fagundes.myshowlist.feat.detail.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveContentDetailUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveFavoriteStateUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.RefreshContentDetailUseCase
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
    private val observeContentDetail: ObserveContentDetailUseCase,
    private val refreshContentDetail: RefreshContentDetailUseCase,
    private val observeFavoriteState: ObserveFavoriteStateUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val saveRecentMovie: SaveRecentMovieUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DetailEvent>()
    val events: SharedFlow<DetailEvent> = _events.asSharedFlow()
    private val latestFavoriteState = MutableStateFlow(false)

    init {
        observeFavorite()
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

            toggleFavorite(current.ui.toContentItem(type))
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

    private fun observeFavorite() {
        viewModelScope.launch {
            observeFavoriteState(id, type).collect { isFavorite ->
                latestFavoriteState.value = isFavorite
                _uiState.update { state ->
                    (state as? DetailUiState.Success)?.copy(isFavorite = isFavorite) ?: state
                }
            }
        }
    }

    private fun observeDetail() {
        viewModelScope.launch {
            observeContentDetail(id, type).collect { detail ->
                if (detail != null) {
                    saveRecentMovie(detail.toContentItem(type))
                    _uiState.value = DetailUiState.Success(ui = detail, isFavorite = latestFavoriteState.value)
                }
            }
        }
    }

    private fun refreshDetail() {
        viewModelScope.launch {
            refreshContentDetail(id)
                .onFailure {
                    if (_uiState.value is DetailUiState.Loading) {
                        _uiState.value = DetailUiState.Error("Failed to load content")
                    }
                }
        }
    }
}

private fun ContentDetail.toContentItem(type: ContentType): ContentItem =
    ContentItem(
        id = id,
        type = type,
        title = title,
        posterUrl = imageUrl,
        overview = overview,
        rating = rating,
    )
