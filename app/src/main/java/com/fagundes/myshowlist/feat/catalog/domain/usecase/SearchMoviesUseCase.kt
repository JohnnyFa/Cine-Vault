package com.fagundes.myshowlist.feat.catalog.domain.usecase

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.domain.repository.CatalogRepository

class SearchMoviesUseCase(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(query: String): Result<List<Movie>> = repository.searchMoviesByName(query)
}
