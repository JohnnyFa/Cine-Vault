package com.fagundes.myshowlist.feat.home.domain.model

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.Movie
import kotlin.math.pow

/**
 * What the user has shown interest in, read from favorites and recents.
 *
 * [seeds] are the movies recommendations are drawn from, strongest first. [seenIds] are the
 * movie ids left out of the result, so "For You" never suggests a movie the user already opened
 * or saved.
 */
data class TasteProfile(
    val seeds: List<Seed>,
    val seenIds: Set<Int>,
) {
    data class Seed(
        val movieId: Int,
        val weight: Double,
    )

    companion object {
        const val MAX_SEEDS = 5
        private const val MAX_FAVORITE_SEEDS = 3

        // A favorite is an explicit "I like this", so it outweighs a view.
        private const val FAVORITE_WEIGHT = 2.0
        private const val RECENT_WEIGHT = 1.0
        private const val RECENT_DECAY = 0.8

        /**
         * Both lists arrive newest first, as the DAOs order them. Only movies can seed: the
         * recommendations endpoint is `movie/{id}/recommendations`.
         */
        fun from(
            favorites: List<Movie>,
            recents: List<Movie>,
        ): TasteProfile {
            // TV and anime ids are separate numbering schemes; mixing them in would hide movies.
            val favoriteMovies = favorites.filter { it.type == ContentType.MOVIE }
            val recentMovies = recents.filter { it.type == ContentType.MOVIE }

            val favoriteSeeds =
                favoriteMovies
                    .take(MAX_FAVORITE_SEEDS)
                    .map { Seed(it.id, FAVORITE_WEIGHT) }

            val favoriteIds = favoriteMovies.mapTo(mutableSetOf()) { it.id }
            val recentSeeds =
                recentMovies
                    .filter { it.id !in favoriteIds }
                    .take(MAX_SEEDS - favoriteSeeds.size)
                    .mapIndexed { index, movie -> Seed(movie.id, RECENT_WEIGHT * RECENT_DECAY.pow(index)) }

            return TasteProfile(
                seeds = favoriteSeeds + recentSeeds,
                seenIds = favoriteIds + recentMovies.map { it.id },
            )
        }
    }
}
