package com.fagundes.myshowlist.feat.home.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.data.repository.HomeRepository
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveFavoritesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecentsUseCase
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

    init {
        observeHomeSections()
        refreshHome()
        observeFavorites()
        observeRecents()
    }

    private fun observeHomeSections() {
        viewModelScope.launch {
            repository.observePopularMovies().collect { movies ->
                _trendingState.value =
                    if (movies.isEmpty()) HomeUiState.Loading else HomeUiState.Success(movies)
            }
        }
        viewModelScope.launch {
            repository.observeRecommendedMovies().collect { movies ->
                _forYouState.value =
                    if (movies.isEmpty()) HomeUiState.Loading else HomeUiState.Success(movies)
            }
        }
        viewModelScope.launch {
            repository.observeShowOfTheDay().collect { movie ->
                _showOfTheDayState.value =
                    movie?.let { HomeUiState.Success(it) } ?: HomeUiState.Loading
            }
        }
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
        viewModelScope.launch {
            runCatching { repository.refreshHomeIfNeeded() }.onFailure {
                if (_trendingState.value is HomeUiState.Loading) {
                    _trendingState.value =
                        HomeUiState.Error("Failed to load trending")
                }
                if (_forYouState.value is HomeUiState.Loading) {
                    _forYouState.value =
                        HomeUiState.Error("Failed to load recommended")
                }
                if (_showOfTheDayState.value is HomeUiState.Loading) {
                    _showOfTheDayState.value =
                        HomeUiState.Error("Failed to load show of the day")
                }
            }
        }
    }

    fun loadPopular() = refreshHome()

    fun loadRecommended() = refreshHome()

    fun loadShowOfTheDay() = refreshHome()
}

sealed interface HomeUiState<out T> {
    object Idle : HomeUiState<Nothing>

    object Loading : HomeUiState<Nothing>

    data class Success<T>(val data: T) : HomeUiState<T>

    data class Error(val message: String) : HomeUiState<Nothing>
}
