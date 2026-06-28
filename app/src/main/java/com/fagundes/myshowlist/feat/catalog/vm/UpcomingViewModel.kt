package com.fagundes.myshowlist.feat.catalog.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.data.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UpcomingViewModel(
    private val repository: CatalogRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UpcomingUiState>(UpcomingUiState.Loading)
    val uiState: StateFlow<UpcomingUiState> = _uiState

    init {
        loadUpcoming()
    }

    fun retry() {
        _uiState.value = UpcomingUiState.Loading
        loadUpcoming()
    }

    private fun loadUpcoming() =
        viewModelScope.launch {
            repository.getUpcomingMovies()
                .onSuccess { movies -> _uiState.value = UpcomingUiState.Content(movies) }
                .onFailure { _uiState.value = UpcomingUiState.Error("Erro ao carregar filmes") }
        }
}

sealed interface UpcomingUiState {
    object Loading : UpcomingUiState

    data class Content(val movies: List<Movie>) : UpcomingUiState

    data class Error(val message: String) : UpcomingUiState
}
