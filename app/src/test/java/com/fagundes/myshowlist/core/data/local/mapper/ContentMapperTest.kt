package com.fagundes.myshowlist.core.data.local.mapper

import com.fagundes.myshowlist.core.data.local.entity.FavoriteEntity
import com.fagundes.myshowlist.core.data.local.entity.RecentEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.ContentItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ContentMapperTest {
    @Test
    fun `favorite keeps its content type so it routes to the right detail screen`() {
        val entity =
            FavoriteEntity(
                id = 7,
                type = ContentType.ANIME,
                title = "Anime 1",
                posterUrl = "/poster.jpg",
                overview = "Overview",
                rating = 9.0,
                favoritedAt = 0L,
            )

        assertEquals(ContentType.ANIME, entity.toMovie().type)
    }

    @Test
    fun `favorite maps every field across`() {
        val entity =
            FavoriteEntity(
                id = 7,
                type = ContentType.MOVIE,
                title = "Movie 1",
                posterUrl = "/poster.jpg",
                overview = "Overview",
                rating = 9.0,
                favoritedAt = 0L,
            )

        val movie = entity.toMovie()

        assertEquals(7, movie.id)
        assertEquals("Movie 1", movie.title)
        assertEquals("/poster.jpg", movie.posterUrl)
        assertEquals("Overview", movie.overview)
        assertEquals(9.0, movie.rating!!, 0.0)
    }

    @Test
    fun `recent keeps its content type`() {
        val entity =
            RecentEntity(
                id = 3,
                type = ContentType.ANIME,
                title = "Anime 2",
                posterUrl = null,
                rating = 7.0,
                viewedAt = 0L,
            )

        assertEquals(ContentType.ANIME, entity.toMovie().type)
    }

    @Test
    fun `content item keeps its type when stored as a recent`() {
        val item =
            ContentItem(
                id = 5,
                type = ContentType.ANIME,
                title = "Anime 3",
                posterUrl = null,
                overview = null,
                rating = null,
            )

        val entity = item.toRecentEntity(viewedAt = 123L)

        assertEquals(ContentType.ANIME, entity.type)
        assertEquals(123L, entity.viewedAt)
    }
}
