package com.fagundes.myshowlist.feat.catalog.domain.usecase

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.domain.model.MovieGenre
import com.fagundes.myshowlist.feat.catalog.domain.repository.CatalogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMoviesByGenreUseCaseTest {
    private val repository: CatalogRepository = mockk(relaxed = true)
    private val useCase = GetMoviesByGenreUseCase(repository)

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke should forward the genre id to the repository`() =
        runTest {
            val movies = listOf(Movie(1, "Action Movie", null, "Action", 9.0))
            coEvery { repository.getMoviesByGenre(MovieGenre.ACTION.genreId!!) } returns Result.success(movies)

            val result = useCase(MovieGenre.ACTION)

            coVerify(exactly = 1) { repository.getMoviesByGenre(MovieGenre.ACTION.genreId!!) }
            assertEquals(movies, result.getOrNull())
        }

    @Test
    fun `invoke should fail without hitting the repository when the genre has no id`() =
        runTest {
            val result = useCase(MovieGenre.ALL)

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            coVerify(exactly = 0) { repository.getMoviesByGenre(any()) }
        }

    @Test
    fun `invoke should propagate a repository failure`() =
        runTest {
            coEvery { repository.getMoviesByGenre(any()) } returns Result.failure(Exception("Network error"))

            val result = useCase(MovieGenre.HORROR)

            assertTrue(result.isFailure)
        }
}
