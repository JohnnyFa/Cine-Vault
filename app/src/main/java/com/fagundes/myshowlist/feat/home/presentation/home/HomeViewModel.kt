package com.fagundes.myshowlist.feat.home.presentation.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.domain.model.TasteProfile
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveFavoritesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecentsUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecommendedMoviesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveShowOfTheDayUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveTrendingMoviesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.RefreshHomeUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.RefreshRecommendationsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val observeTrendingMovies: ObserveTrendingMoviesUseCase,
    private val observeRecommendedMovies: ObserveRecommendedMoviesUseCase,
    private val observeShowOfTheDay: ObserveShowOfTheDayUseCase,
    private val refreshHomeUseCase: RefreshHomeUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val observeRecents: ObserveRecentsUseCase,
    private val refreshRecommendations: RefreshRecommendationsUseCase,
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

    // Bumped to rebuild "For You" from the current profile without waiting for it to change.
    private val recommendationRequests = MutableStateFlow(0)

    init {
        observeHomeSections()
        // The taste profile's first emission already builds "For You"; rebuilding here too would
        // cancel that run and fetch every seed again on each cold start.
        refreshHome(rebuildRecommendations = false)
        observeFavoriteSection()
        observeRecentSection()
        observeTasteProfile()
    }

    // Each delegate is a single-expression call — no nesting, no branching here.
    private fun observeHomeSections() {
        observeListSection(observeTrendingMovies(), _trendingState)
        observeListSection(observeRecommendedMovies(), _forYouState)
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
            observeShowOfTheDay().collect { movie ->
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

    private fun observeFavoriteSection() {
        viewModelScope.launch {
            observeFavorites().collect { movies ->
                _favoritesState.value = HomeUiState.Success(movies)
            }
        }
    }

    private fun observeRecentSection() {
        viewModelScope.launch {
            observeRecents().collect { movies ->
                _recentsState.value = HomeUiState.Success(movies)
            }
        }
    }

    // "For You" follows the user: favoriting or opening a movie changes the profile and rebuilds it.
    // The first emission doubles as the initial load, so it also runs on every app start. Every
    // trigger goes through this one collectLatest, so a newer run always cancels a stale one
    // instead of racing it to the cache.
    private fun observeTasteProfile() {
        viewModelScope.launch {
            val profiles =
                combine(observeFavorites(), observeRecents()) { favorites, recents -> TasteProfile.from(favorites, recents) }
                    .distinctUntilChanged()
            combine(profiles, recommendationRequests) { profile, _ -> profile }
                .collectLatest { profile -> doRefreshRecommendations(profile) }
        }
    }

    private suspend fun doRefreshRecommendations(profile: TasteProfile) {
        val result = refreshRecommendations(profile)
        // runCatching also catches the cancellation collectLatest uses to drop a stale run; that
        // isn't a failure, so stop here instead of flashing an error.
        currentCoroutineContext().ensureActive()
        result.onFailure { _forYouState.setErrorIfLoading(R.string.fail_load_recommended) }
    }

    private fun refreshHome(rebuildRecommendations: Boolean = true) {
        if (homeRefreshJob?.isActive == true) return
        homeRefreshJob = viewModelScope.launch { doRefreshHome(rebuildRecommendations) }
    }

    private suspend fun doRefreshHome(rebuildRecommendations: Boolean) {
        refreshHomeUseCase()
            .onFailure {
                _trendingState.setErrorIfLoading(R.string.fail_load_popular_movies)
                _showOfTheDayState.setErrorIfLoading(R.string.fail_load_show_of_the_day)
            }
        if (rebuildRecommendations) recommendationRequests.update { it + 1 }
    }

    fun loadPopular() = refreshHome()

    fun loadRecommended() = refreshHome()

    fun loadShowOfTheDay() = refreshHome()
}

// Replaces the repeated if-is-Loading-then-set-Error pattern inside onFailure.
private fun <T> MutableStateFlow<HomeUiState<T>>.setErrorIfLoading(
    @StringRes message: Int,
) {
    if (value is HomeUiState.Loading) value = HomeUiState.Error(message)
}
