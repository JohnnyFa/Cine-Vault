package com.fagundes.myshowlist.feat.catalog.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.data.repository.CatalogRepository
import com.fagundes.myshowlist.feat.catalog.domain.MovieGenre
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: CatalogRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var baseMovies: List<Movie> = emptyList()

    init {
        Log.d("CatalogViewModel", "CatalogViewModel init: ${this.hashCode()}")
        if (_uiState.value !is CatalogUiState.Content) {
            loadCatalog()
        }
        observeSearch()
    }

    fun onSeeAllUpcoming() {
        print("implementacao futura")
    }

    fun retry() {
        _uiState.value = CatalogUiState.Loading
        loadCatalog()
    }

    fun onCategorySelected(category: MovieGenre) {
        val current =
            (_uiState.value as? CatalogUiState.Content)?.ui
                ?: CatalogContentState()

        if (category == MovieGenre.ALL) {
            loadCatalog()
            return
        }

        viewModelScope.launch {
            repository.getMoviesByCategory(category.genreId!!)
                .onSuccess { movies ->
                    baseMovies = movies
                    _uiState.value =
                        CatalogUiState.Content(
                            current.copy(
                                selectedCategory = category,
                                movies = movies,
                                featuredMovie = movies.randomOrNull(),
                            ),
                        )
                }
                .onFailure {
                    _uiState.value =
                        CatalogUiState.Error("Erro ao carregar categoria")
                }
        }
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query

        val current =
            (_uiState.value as? CatalogUiState.Content)?.ui
                ?: CatalogContentState()

        if (query.isBlank()) {
            _uiState.value =
                CatalogUiState.Content(
                    current.copy(
                        movies = baseMovies,
                    ),
                )
            return
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(600)
                .collectLatest { query ->
                    if (query.isBlank()) return@collectLatest

                    if (query.length < 2) {
                        val current =
                            (_uiState.value as? CatalogUiState.Content)?.ui
                                ?: CatalogContentState()
                        _uiState.value =
                            CatalogUiState.Content(
                                current.copy(movies = baseMovies),
                            )
                        return@collectLatest
                    }

                    repository.searchMoviesByName(query)
                        .onSuccess { movies ->
                            val current =
                                (_uiState.value as? CatalogUiState.Content)?.ui
                                    ?: CatalogContentState()

                            _uiState.value =
                                CatalogUiState.Content(
                                    current.copy(
                                        movies = movies,
                                    ),
                                )
                        }
                }
        }
    }

    private fun loadCatalog() =
        viewModelScope.launch {
            repository.getUpcomingMovies()
                .onSuccess { movies ->
                    baseMovies = movies
                    _uiState.value =
                        CatalogUiState.Content(
                            CatalogContentState(
                                movies = movies,
                                featuredMovie = movies.randomOrNull(),
                            ),
                        )
                }
                .onFailure {
                    _uiState.value = CatalogUiState.Error("Erro ao carregar catálogo")
                }
        }
}

sealed interface CatalogUiState {
    object Loading : CatalogUiState

    data class Content(
        val ui: CatalogContentState,
    ) : CatalogUiState

    data class Error(
        val message: String,
    ) : CatalogUiState
}

data class CatalogContentState(
    val selectedCategory: MovieGenre = MovieGenre.ALL,
    val movies: List<Movie> = emptyList(),
    val featuredMovie: Movie? = null,
)
