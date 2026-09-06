package com.fagundes.myshowlist.feat.catalog.presentation.catalog

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.domain.model.MovieGenre

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

sealed interface CatalogNavEvent {
    object SeeAllUpcoming : CatalogNavEvent
}
