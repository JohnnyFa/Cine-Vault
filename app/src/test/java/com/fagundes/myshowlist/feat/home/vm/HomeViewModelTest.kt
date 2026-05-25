package com.fagundes.myshowlist.feat.home.vm

import android.util.Log
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.data.repository.HomeRepository
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveFavoritesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecentsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val repository: HomeRepository = mockk(relaxed = true)
    private val observeFavoritesUseCase: ObserveFavoritesUseCase = mockk(relaxed = true)
    private val observeRecentsUseCase: ObserveRecentsUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    private val movie = Movie(1, "Test Movie", "url", "Overview", 8.0)
    private val movieList = listOf(movie)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        // Set up default successful responses for init block
        every { repository.observePopularMovies() } returns flowOf(movieList)
        every { repository.observeRecommendedMovies() } returns flowOf(movieList)
        every { repository.observeShowOfTheDay() } returns flowOf(movie)
        every { observeFavoritesUseCase() } returns flowOf(movieList)
        every { observeRecentsUseCase() } returns flowOf(movieList)

        viewModel = HomeViewModel(repository, observeFavoritesUseCase, observeRecentsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should load data correctly`() =
        runTest {
            testDispatcher.scheduler.runCurrent()

            assert(viewModel.trendingState.value is HomeUiState.Success)
            assertEquals(movieList, (viewModel.trendingState.value as HomeUiState.Success).data)

            assert(viewModel.forYouState.value is HomeUiState.Success)
            assertEquals(movieList, (viewModel.forYouState.value as HomeUiState.Success).data)

            assert(viewModel.showOfTheDayState.value is HomeUiState.Success)
            assertEquals(movie, (viewModel.showOfTheDayState.value as HomeUiState.Success<Movie>).data)

            assert(viewModel.favoritesState.value is HomeUiState.Success)
            assertEquals(movieList, (viewModel.favoritesState.value as HomeUiState.Success).data)

            assert(viewModel.recentsState.value is HomeUiState.Success)
            assertEquals(movieList, (viewModel.recentsState.value as HomeUiState.Success).data)
        }

    @Test
    fun `loadPopular should call refreshHomeIfNeeded`() =
        runTest {
            viewModel.loadPopular()
            testDispatcher.scheduler.runCurrent()

            coEvery { repository.refreshHomeIfNeeded() }
        }

    @Test
    fun `loadPopular should update trendingState to Error on failure if still loading`() =
        runTest {
            // Force loading state for this test
            every { repository.observePopularMovies() } returns flowOf(emptyList())
            viewModel = HomeViewModel(repository, observeFavoritesUseCase, observeRecentsUseCase)
            testDispatcher.scheduler.runCurrent()

            coEvery { repository.refreshHomeIfNeeded() } throws Exception("Network error")

            viewModel.loadPopular()
            testDispatcher.scheduler.runCurrent()

            assert(viewModel.trendingState.value is HomeUiState.Error)
            assertEquals("Failed to load trending", (viewModel.trendingState.value as HomeUiState.Error).message)
        }
}
