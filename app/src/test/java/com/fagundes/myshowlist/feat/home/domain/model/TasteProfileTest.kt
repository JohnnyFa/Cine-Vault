package com.fagundes.myshowlist.feat.home.domain.model

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TasteProfileTest {
    private fun movie(
        id: Int,
        type: ContentType = ContentType.MOVIE,
    ) = Movie(id, "Movie $id", null, null, null, type)

    @Test
    fun `no favorites and no recents should give an empty profile`() {
        val profile = TasteProfile.from(emptyList(), emptyList())

        assertTrue(profile.seeds.isEmpty())
        assertTrue(profile.seenIds.isEmpty())
    }

    @Test
    fun `favorites should seed first and outweigh recents`() {
        val profile = TasteProfile.from(favorites = listOf(movie(1)), recents = listOf(movie(2)))

        assertEquals(listOf(1, 2), profile.seeds.map { it.movieId })
        assertTrue(profile.seeds[0].weight > profile.seeds[1].weight)
    }

    @Test
    fun `older recents should weigh less than newer ones`() {
        val profile = TasteProfile.from(favorites = emptyList(), recents = listOf(movie(1), movie(2), movie(3)))

        val weights = profile.seeds.map { it.weight }
        assertEquals(weights.sortedDescending(), weights)
        assertTrue(weights[0] > weights[2])
    }

    @Test
    fun `seeds should be capped, with at most three favorites`() {
        val favorites = (1..4).map { movie(it) }
        val recents = (10..14).map { movie(it) }

        val profile = TasteProfile.from(favorites, recents)

        assertEquals(TasteProfile.MAX_SEEDS, profile.seeds.size)
        assertEquals(listOf(1, 2, 3, 10, 11), profile.seeds.map { it.movieId })
    }

    @Test
    fun `a recent that is also a favorite should seed only once`() {
        val profile = TasteProfile.from(favorites = listOf(movie(1)), recents = listOf(movie(1), movie(2)))

        assertEquals(listOf(1, 2), profile.seeds.map { it.movieId })
    }

    @Test
    fun `non-movie content should neither seed nor count as seen`() {
        val profile =
            TasteProfile.from(
                favorites = listOf(movie(1, ContentType.TV)),
                recents = listOf(movie(2, ContentType.ANIME), movie(3)),
            )

        // TV id 1 and anime id 2 aren't movie ids, so they must not hide movies 1 and 2.
        assertEquals(listOf(3), profile.seeds.map { it.movieId })
        assertEquals(setOf(3), profile.seenIds)
    }

    @Test
    fun `every favorite and recent should be marked as seen, beyond the seed cap`() {
        val favorites = (1..4).map { movie(it) }
        val recents = (10..14).map { movie(it) }

        val profile = TasteProfile.from(favorites, recents)

        assertEquals((1..4).toSet() + (10..14).toSet(), profile.seenIds)
    }

    @Test
    fun `a favorite TV show sharing a movie's id should not stop that movie from seeding`() {
        val profile = TasteProfile.from(favorites = listOf(movie(7, ContentType.TV)), recents = listOf(movie(7)))

        assertEquals(listOf(7), profile.seeds.map { it.movieId })
    }
}
