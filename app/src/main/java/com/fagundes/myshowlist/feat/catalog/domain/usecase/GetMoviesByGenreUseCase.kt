package com.fagundes.myshowlist.feat.catalog.domain.usecase

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.domain.model.MovieGenre
import com.fagundes.myshowlist.feat.catalog.domain.repository.CatalogRepository

class GetMoviesByGenreUseCase(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(genre: MovieGenre): Result<List<Movie>> {
        val genreId =
            genre.genreId
                ?: return Result.failure(
                    IllegalArgumentException("${genre.name} maps to no TMDB genre id"),
                )

        return repository.getMoviesByGenre(genreId)
    }
}
