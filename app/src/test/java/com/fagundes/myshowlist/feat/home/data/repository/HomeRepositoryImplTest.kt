package com.fagundes.myshowlist.feat.home.data.repository

import com.fagundes.myshowlist.core.data.local.datasource.ContentLocalDataSource
import com.fagundes.myshowlist.core.data.local.entity.ContentEntity
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.data.remote.HomeRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeRepositoryImplTest {
    private val local: ContentLocalDataSource = mockk(relaxed = true)
    private val remote: HomeRemoteDataSource = mockk()
    private val repository = HomeRepositoryImpl(local = local, remote = remote)

    private fun movie(id: Int) = Movie(id, "Movie $id", null, null, null)

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `saveRecommendedMovies should stamp cachedAt so ORDER BY cachedAt DESC keeps the rank`() =
        runTest {
            val saved = slot<List<ContentEntity>>()
            coEvery { local.saveMoviesForCategory(ContentCategory.RECOMMENDED, capture(saved)) } returns Unit
            val ranked = listOf(movie(30), movie(10), movie(20))

            repository.saveRecommendedMovies(ranked)

            // SQLite gives no order for ties, so each rank needs its own, strictly older timestamp.
            val stamps = saved.captured.map { it.cachedAt }
            assertEquals(listOf(30, 10, 20), saved.captured.map { it.id })
            assertEquals(listOf(1L, 1L), stamps.zipWithNext { newer, older -> newer - older })
            assertTrue(saved.captured.all { it.category == ContentCategory.RECOMMENDED })
        }

    @Test
    fun `getRecommendationsFor should hit the network once per seed`() =
        runTest {
            coEvery { remote.getRecommendationsFor(1) } returns listOf(movie(100))

            repository.getRecommendationsFor(1)
            val second = repository.getRecommendationsFor(1)

            assertEquals(listOf(movie(100)), second.getOrNull())
            coVerify(exactly = 1) { remote.getRecommendationsFor(1) }
        }

    @Test
    fun `a failed seed fetch should not be cached`() =
        runTest {
            coEvery { remote.getRecommendationsFor(1) } throws RuntimeException("Network error") andThen listOf(movie(100))

            val first = repository.getRecommendationsFor(1)
            val second = repository.getRecommendationsFor(1)

            assertTrue(first.isFailure)
            assertEquals(listOf(movie(100)), second.getOrNull())
            coVerify(exactly = 2) { remote.getRecommendationsFor(1) }
        }

    @Test
    fun `getTopRatedMovies should wrap a network failure in Result`() =
        runTest {
            coEvery { remote.getTopRatedMovies() } throws RuntimeException("Network error")

            assertTrue(repository.getTopRatedMovies().isFailure)
        }
}
