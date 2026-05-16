package com.fagundes.myshowlist.feat.home.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.data.repository.HomeRepository
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveFavoritesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecentsUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val auth: FirebaseAuth,
    private val repository: HomeRepository,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val observeRecentsUseCase: ObserveRecentsUseCase
) : ViewModel() {

    private val _trendingState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
    val trendingState: StateFlow<HomeUiState<List<Movie>>> = _trendingState

    private val _forYouState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
    val forYouState: StateFlow<HomeUiState<List<Movie>>> = _forYouState

    private val _showOfTheDayState = MutableStateFlow<HomeUiState<Movie>>(HomeUiState.Idle)
    val showOfTheDay: StateFlow<HomeUiState<Movie>> = _showOfTheDayState

    private val _favoritesState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
    val favoritesState: StateFlow<HomeUiState<List<Movie>>> = _favoritesState

    private val _recentsState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
    val recentsState: StateFlow<HomeUiState<List<Movie>>> = _recentsState

    init {
        Log.d("HomeViewModel", "HomeViewModel init: ${this.hashCode()}")
        if (_trendingState.value !is HomeUiState.Success) loadPopular()
        if (_forYouState.value !is HomeUiState.Success) loadRecommended()
        if (_showOfTheDayState.value !is HomeUiState.Success) loadShowOfTheDay()
        observeFavorites()
        observeRecents()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoritesUseCase().collect { favorites ->
                _favoritesState.value = HomeUiState.Success(favorites)
            }
        }
    }

    private fun observeRecents() {
        viewModelScope.launch {
            observeRecentsUseCase().collect { recents ->
                _recentsState.value = HomeUiState.Success(recents)
            }
        }
    }

    fun loadPopular() {
        Log.d("HomeViewModel", "loadPopular called")
        viewModelScope.launch {
            _trendingState.value = HomeUiState.Loading

            repository.getPopularMovies()
                .onSuccess {
                    _trendingState.value = HomeUiState.Success(it)
                }
                .onFailure {
                    _trendingState.value =
                        HomeUiState.Error("Failed to load trending")
                }
        }
    }

    fun loadRecommended() {
        Log.d("HomeViewModel", "loadRecommended called")
        viewModelScope.launch {
            _forYouState.value = HomeUiState.Loading

            repository.getRecommended()
                .onSuccess {
                    _forYouState.value = HomeUiState.Success(it)
                }
                .onFailure {
                    _forYouState.value =
                        HomeUiState.Error("Failed to load recommended")
                }

        }
    }

    fun loadShowOfTheDay() {
        Log.d("HomeViewModel", "loadShowOfTheDay called")
        viewModelScope.launch {
            _showOfTheDayState.value = HomeUiState.Loading

            repository.getShowOfTheDay()
                .onSuccess {
                    _showOfTheDayState.value = HomeUiState.Success(it)
                }
                .onFailure {
                    _showOfTheDayState.value =
                        HomeUiState.Error("Failed to load show of the day")
                }
        }
    }

    fun logout() {
        auth.signOut()
    }
}

sealed interface HomeUiState<out T> {
    object Idle : HomeUiState<Nothing>
    object Loading : HomeUiState<Nothing>
    data class Success<T>(val data: T) : HomeUiState<T>
    data class Error(val message: String) : HomeUiState<Nothing>
}
