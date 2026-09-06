package com.fagundes.myshowlist.feat.catalog.presentation.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.feat.catalog.domain.usecase.GetUpcomingMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UpcomingViewModel(
    private val getUpcomingMovies: GetUpcomingMoviesUseCase,
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
            getUpcomingMovies()
                .onSuccess { movies -> _uiState.value = UpcomingUiState.Content(movies) }
                .onFailure { _uiState.value = UpcomingUiState.Error("Erro ao carregar filmes") }
        }
}
