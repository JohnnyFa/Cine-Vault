package com.fagundes.myshowlist.feat.home.domain.usecase

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.domain.model.TasteProfile
import com.fagundes.myshowlist.feat.home.domain.repository.HomeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Rebuilds "For You" from the user's [TasteProfile] and caches it.
 *
 * Each seed's TMDB recommendations are merged and scored: a movie suggested by several seeds,
 * by heavier seeds, or near the top of a seed's list ranks higher. With no seeds yet (a new
 * user or guest) or nothing left after removing what they've seen, top-rated movies stand in.
 */
class RefreshRecommendationsUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(profile: TasteProfile): Result<Unit> {
        val movies = if (profile.seeds.isEmpty()) repository.getTopRatedMovies() else recommendFrom(profile)
        return movies.mapCatching { repository.saveRecommendedMovies(it).getOrThrow() }
    }

    private suspend fun recommendFrom(profile: TasteProfile): Result<List<Movie>> {
        val results =
            coroutineScope {
                profile.seeds
                    .map { seed -> async { seed to repository.getRecommendationsFor(seed.movieId) } }
                    .awaitAll()
            }

        // One seed failing (a delisted movie, a flaky request) shouldn't empty the section.
        val succeeded = results.mapNotNull { (seed, result) -> result.getOrNull()?.let { seed to it } }
        if (succeeded.isEmpty()) return Result.failure(results.firstNotNullOf { it.second.exceptionOrNull() })

        val ranked = rank(succeeded, profile.seenIds)
        return if (ranked.isEmpty()) repository.getTopRatedMovies() else Result.success(ranked)
    }

    private fun rank(
        recommendations: List<Pair<TasteProfile.Seed, List<Movie>>>,
        seenIds: Set<Int>,
    ): List<Movie> {
        val scores = mutableMapOf<Int, Double>()
        // LinkedHashMap keeps first-seen order, which breaks score ties deterministically.
        val movies = linkedMapOf<Int, Movie>()

        for ((seed, list) in recommendations) {
            list.forEachIndexed { index, movie ->
                if (movie.id in seenIds) return@forEachIndexed
                // TMDB orders its own suggestions; the first counts fully, the last about half.
                val positionFactor = 1.0 - POSITION_PENALTY * index / list.size
                scores[movie.id] = (scores[movie.id] ?: 0.0) + seed.weight * positionFactor
                movies.putIfAbsent(movie.id, movie)
            }
        }

        return movies.values
            .sortedByDescending { scores.getValue(it.id) }
            .take(MAX_RESULTS)
    }

    private companion object {
        const val MAX_RESULTS = 20
        const val POSITION_PENALTY = 0.5
    }
}
