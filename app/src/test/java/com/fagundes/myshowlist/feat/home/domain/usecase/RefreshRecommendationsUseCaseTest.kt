package com.fagundes.myshowlist.feat.home.domain.usecase

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.domain.model.TasteProfile
import com.fagundes.myshowlist.feat.home.domain.model.TasteProfile.Seed
import com.fagundes.myshowlist.feat.home.domain.repository.HomeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RefreshRecommendationsUseCaseTest {
    private val repository: HomeRepository = mockk()
    private val useCase = RefreshRecommendationsUseCase(repository)
    private val saved = slot<List<Movie>>()

    private fun movie(id: Int) = Movie(id, "Movie $id", null, null, null)

    private fun movies(vararg ids: Int) = ids.map { movie(it) }

    @Before
    fun setup() {
        coEvery { repository.saveRecommendedMovies(capture(saved)) } returns Result.success(Unit)
        coEvery { repository.getTopRatedMovies() } returns Result.success(movies(900, 901))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `an empty profile should fall back to top rated without asking for recommendations`() =
        runTest {
            val result = useCase(TasteProfile(seeds = emptyList(), seenIds = emptySet()))

            assertTrue(result.isSuccess)
            assertEquals(movies(900, 901), saved.captured)
            coVerify(exactly = 0) { repository.getRecommendationsFor(any()) }
        }

    @Test
    fun `a movie suggested by several seeds should outrank one suggested by a single seed`() =
        runTest {
            coEvery { repository.getRecommendationsFor(1) } returns Result.success(movies(100, 200))
            coEvery { repository.getRecommendationsFor(2) } returns Result.success(movies(300, 200))

            useCase(TasteProfile(seeds = listOf(Seed(1, 1.0), Seed(2, 1.0)), seenIds = setOf(1, 2)))

            assertEquals(200, saved.captured.first().id)
            assertEquals(setOf(100, 200, 300), saved.captured.map { it.id }.toSet())
        }

    @Test
    fun `a heavier seed's suggestions should rank above a lighter seed's`() =
        runTest {
            coEvery { repository.getRecommendationsFor(1) } returns Result.success(movies(100))
            coEvery { repository.getRecommendationsFor(2) } returns Result.success(movies(200))

            useCase(TasteProfile(seeds = listOf(Seed(1, 1.0), Seed(2, 2.0)), seenIds = emptySet()))

            assertEquals(listOf(200, 100), saved.captured.map { it.id })
        }

    @Test
    fun `within one seed, TMDB's order should be kept`() =
        runTest {
            coEvery { repository.getRecommendationsFor(1) } returns Result.success(movies(30, 10, 20))

            useCase(TasteProfile(seeds = listOf(Seed(1, 1.0)), seenIds = emptySet()))

            assertEquals(listOf(30, 10, 20), saved.captured.map { it.id })
        }

    @Test
    fun `movies the user has already seen should be left out`() =
        runTest {
            coEvery { repository.getRecommendationsFor(1) } returns Result.success(movies(100, 5, 200))

            useCase(TasteProfile(seeds = listOf(Seed(1, 1.0)), seenIds = setOf(1, 5)))

            assertEquals(listOf(100, 200), saved.captured.map { it.id })
        }

    @Test
    fun `the result should be capped at twenty movies`() =
        runTest {
            coEvery { repository.getRecommendationsFor(1) } returns Result.success((100 until 130).map { movie(it) })

            useCase(TasteProfile(seeds = listOf(Seed(1, 1.0)), seenIds = emptySet()))

            assertEquals(20, saved.captured.size)
            assertEquals(100, saved.captured.first().id)
        }

    @Test
    fun `one failing seed should not drop the others`() =
        runTest {
            coEvery { repository.getRecommendationsFor(1) } returns Result.failure(Exception("404"))
            coEvery { repository.getRecommendationsFor(2) } returns Result.success(movies(200))

            val result = useCase(TasteProfile(seeds = listOf(Seed(1, 2.0), Seed(2, 1.0)), seenIds = emptySet()))

            assertTrue(result.isSuccess)
            assertEquals(listOf(200), saved.captured.map { it.id })
        }

    @Test
    fun `every seed failing should fail without overwriting the cache`() =
        runTest {
            coEvery { repository.getRecommendationsFor(any()) } returns Result.failure(Exception("Network error"))

            val result = useCase(TasteProfile(seeds = listOf(Seed(1, 1.0), Seed(2, 1.0)), seenIds = emptySet()))

            assertTrue(result.isFailure)
            assertEquals("Network error", result.exceptionOrNull()?.message)
            coVerify(exactly = 0) { repository.saveRecommendedMovies(any()) }
        }

    @Test
    fun `nothing left after filtering seen movies should fall back to top rated`() =
        runTest {
            coEvery { repository.getRecommendationsFor(1) } returns Result.success(movies(5))

            useCase(TasteProfile(seeds = listOf(Seed(1, 1.0)), seenIds = setOf(1, 5)))

            assertEquals(movies(900, 901), saved.captured)
        }

    @Test
    fun `a save failure should surface as a failure`() =
        runTest {
            coEvery { repository.getRecommendationsFor(1) } returns Result.success(movies(100))
            coEvery { repository.saveRecommendedMovies(any()) } returns Result.failure(Exception("SQLite failure"))

            val result = useCase(TasteProfile(seeds = listOf(Seed(1, 1.0)), seenIds = emptySet()))

            assertTrue(result.isFailure)
        }
}
