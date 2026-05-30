package com.fagundes.myshowlist.feat.home.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.data.repository.HomeRepository
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveFavoritesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecentsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val observeRecentsUseCase: ObserveRecentsUseCase,
) : ViewModel() {
    private val _trendingState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
    val trendingState: StateFlow<HomeUiState<List<Movie>>> = _trendingState.asStateFlow()

    private val _forYouState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
    val forYouState: StateFlow<HomeUiState<List<Movie>>> = _forYouState.asStateFlow()

    private val _showOfTheDayState = MutableStateFlow<HomeUiState<Movie>>(HomeUiState.Idle)
    val showOfTheDayState: StateFlow<HomeUiState<Movie>> = _showOfTheDayState.asStateFlow()

    private val _favoritesState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
    val favoritesState: StateFlow<HomeUiState<List<Movie>>> = _favoritesState.asStateFlow()

    private val _recentsState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
    val recentsState: StateFlow<HomeUiState<List<Movie>>> = _recentsState.asStateFlow()

    private var homeRefreshJob: Job? = null

    init {
        observeHomeSections()
        refreshHome()
        observeFavorites()
        observeRecents()
    }

    // Each delegate is a single-expression call — no nesting, no branching here.
    private fun observeHomeSections() {
        observeListSection(repository.observePopularMovies(), _trendingState)
        observeListSection(repository.observeRecommendedMovies(), _forYouState)
        observeShowOfTheDaySection()
    }

    private fun observeListSection(
        flow: Flow<List<Movie>>,
        state: MutableStateFlow<HomeUiState<List<Movie>>>,
    ) {
        viewModelScope.launch {
            flow.collect { movies ->
                if (movies.isEmpty()) {
                    refreshIfPreviouslyLoaded(state.value)
                    state.value = HomeUiState.Loading
                } else {
                    state.value = HomeUiState.Success(movies)
                }
            }
        }
    }

    private fun observeShowOfTheDaySection() {
        viewModelScope.launch {
            repository.observeShowOfTheDay().collect { movie ->
                if (movie == null) {
                    refreshIfPreviouslyLoaded(_showOfTheDayState.value)
                    _showOfTheDayState.value = HomeUiState.Loading
                } else {
                    _showOfTheDayState.value = HomeUiState.Success(movie)
                }
            }
        }
    }

    // Elevates the nested "if was loaded → refresh" check to a named concept.
    private fun refreshIfPreviouslyLoaded(currentState: HomeUiState<*>) {
        if (currentState is HomeUiState.Success) refreshHome()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoritesUseCase().collect { movies ->
                _favoritesState.value = HomeUiState.Success(movies)
            }
        }
    }

    private fun observeRecents() {
        viewModelScope.launch {
            observeRecentsUseCase().collect { movies ->
                _recentsState.value = HomeUiState.Success(movies)
            }
        }
    }

    private fun refreshHome() {
        if (homeRefreshJob?.isActive == true) return
        homeRefreshJob = viewModelScope.launch { doRefreshHome() }
    }

    private suspend fun doRefreshHome() {
        runCatching { repository.refreshHomeIfNeeded() }
            .onFailure {
                _trendingState.setErrorIfLoading("Failed to load trending")
                _forYouState.setErrorIfLoading("Failed to load recommended")
                _showOfTheDayState.setErrorIfLoading("Failed to load show of the day")
            }
    }

    fun loadPopular() = refreshHome()

    fun loadRecommended() = refreshHome()

    fun loadShowOfTheDay() = refreshHome()
}

// Replaces the repeated if-is-Loading-then-set-Error pattern inside onFailure.
private fun <T> MutableStateFlow<HomeUiState<T>>.setErrorIfLoading(message: String) {
    if (value is HomeUiState.Loading) value = HomeUiState.Error(message)
}

sealed interface HomeUiState<out T> {
    object Idle : HomeUiState<Nothing>

    object Loading : HomeUiState<Nothing>

    data class Success<T>(val data: T) : HomeUiState<T>

    data class Error(val message: String) : HomeUiState<Nothing>
}
