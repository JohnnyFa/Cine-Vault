package com.fagundes.myshowlist.feat.catalog.vm

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.data.repository.CatalogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpcomingViewModelTest {
    private val repository: CatalogRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: UpcomingViewModel

    private val movies =
        listOf(
            Movie(1, "Movie 1", null, "Overview 1", 8.0),
            Movie(2, "Movie 2", null, "Overview 2", 7.5),
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getUpcomingMovies() } returns Result.success(movies)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `init should load upcoming movies`() =
        runTest {
            viewModel = UpcomingViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 1) { repository.getUpcomingMovies() }
            val state = viewModel.uiState.value
            assertTrue(state is UpcomingUiState.Content)
            assertEquals(movies, (state as UpcomingUiState.Content).movies)
        }

    @Test
    fun `init failure should show error state`() =
        runTest {
            coEvery { repository.getUpcomingMovies() } returns Result.failure(Exception("Network error"))

            viewModel = UpcomingViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value
            assertTrue(state is UpcomingUiState.Error)
            assertEquals("Erro ao carregar filmes", (state as UpcomingUiState.Error).message)
        }

    @Test
    fun `retry should reload upcoming movies`() =
        runTest {
            viewModel = UpcomingViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.retry()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 2) { repository.getUpcomingMovies() }
        }

    @Test
    fun `retry should set Loading state before reloading`() =
        runTest {
            viewModel = UpcomingViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.retry()

            assertEquals(UpcomingUiState.Loading, viewModel.uiState.value)
        }
}
