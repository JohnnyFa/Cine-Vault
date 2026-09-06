package com.fagundes.myshowlist.feat.catalog.domain.usecase

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.domain.repository.CatalogRepository

class GetUpcomingMoviesUseCase(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(): Result<List<Movie>> = repository.getUpcomingMovies()
}
