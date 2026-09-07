package com.fagundes.myshowlist.feat.home.presentation.home

import android.util.Log
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveFavoritesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecentsUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecommendedMoviesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveShowOfTheDayUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveTrendingMoviesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.RefreshHomeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
class HomeViewModelTest {
    private val observeTrendingMovies: ObserveTrendingMoviesUseCase = mockk()
    private val observeRecommendedMovies: ObserveRecommendedMoviesUseCase = mockk()
    private val observeShowOfTheDay: ObserveShowOfTheDayUseCase = mockk()
    private val refreshHomeUseCase: RefreshHomeUseCase = mockk()
    private val observeFavorites: ObserveFavoritesUseCase = mockk()
    private val observeRecents: ObserveRecentsUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    private val movie = Movie(1, "Test Movie", "url", "Overview", 8.0)
    private val movieList = listOf(movie)

    private fun buildViewModel() =
        HomeViewModel(
            observeTrendingMovies = observeTrendingMovies,
            observeRecommendedMovies = observeRecommendedMovies,
            observeShowOfTheDay = observeShowOfTheDay,
            refreshHomeUseCase = refreshHomeUseCase,
            observeFavorites = observeFavorites,
            observeRecents = observeRecents,
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        // The init block collects eagerly — every collaborator must be stubbed before construction.
        every { observeTrendingMovies() } returns flowOf(movieList)
        every { observeRecommendedMovies() } returns flowOf(movieList)
        every { observeShowOfTheDay() } returns flowOf(movie)
        every { observeFavorites() } returns flowOf(movieList)
        every { observeRecents() } returns flowOf(movieList)
        coEvery { refreshHomeUseCase() } returns Result.success(Unit)

        viewModel = buildViewModel()
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

            assertTrue(viewModel.trendingState.value is HomeUiState.Success)
            assertEquals(movieList, (viewModel.trendingState.value as HomeUiState.Success).data)

            assertTrue(viewModel.forYouState.value is HomeUiState.Success)
            assertEquals(movieList, (viewModel.forYouState.value as HomeUiState.Success).data)

            assertTrue(viewModel.showOfTheDayState.value is HomeUiState.Success)
            assertEquals(movie, (viewModel.showOfTheDayState.value as HomeUiState.Success<Movie>).data)

            assertTrue(viewModel.favoritesState.value is HomeUiState.Success)
            assertEquals(movieList, (viewModel.favoritesState.value as HomeUiState.Success).data)

            assertTrue(viewModel.recentsState.value is HomeUiState.Success)
            assertEquals(movieList, (viewModel.recentsState.value as HomeUiState.Success).data)
        }

    @Test
    fun `init should refresh home once`() =
        runTest {
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 1) { refreshHomeUseCase() }
        }

    @Test
    fun `loadPopular should trigger a refresh`() =
        runTest {
            testDispatcher.scheduler.runCurrent()

            viewModel.loadPopular()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 2) { refreshHomeUseCase() }
        }

    @Test
    fun `loadRecommended and loadShowOfTheDay should each trigger a refresh`() =
        runTest {
            testDispatcher.scheduler.runCurrent()

            viewModel.loadRecommended()
            testDispatcher.scheduler.runCurrent()
            viewModel.loadShowOfTheDay()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 3) { refreshHomeUseCase() }
        }

    @Test
    fun `refresh should not re-enter while one is already in flight`() =
        runTest {
            testDispatcher.scheduler.runCurrent()
            coVerify(exactly = 1) { refreshHomeUseCase() }

            // Three calls with no scheduler advance in between: the first launches a job, the
            // other two must see it still active and bail out. Without the guard this adds 3.
            viewModel.loadPopular()
            viewModel.loadRecommended()
            viewModel.loadShowOfTheDay()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 2) { refreshHomeUseCase() }
        }

    @Test
    fun `loadPopular should update trendingState to Error on failure if still loading`() =
        runTest {
            every { observeTrendingMovies() } returns flowOf(emptyList())
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            coEvery { refreshHomeUseCase() } returns Result.failure(Exception("Network error"))

            viewModel.loadPopular()
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.trendingState.value is HomeUiState.Error)
            assertEquals("Failed to load trending", (viewModel.trendingState.value as HomeUiState.Error).message)
        }

    @Test
    fun `should auto-refresh and enter Loading when popular movies are wiped from cache after successful load`() =
        runTest {
            val popularFlow = MutableStateFlow(movieList)
            every { observeTrendingMovies() } returns popularFlow
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.trendingState.value is HomeUiState.Success)

            popularFlow.value = emptyList()
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.trendingState.value is HomeUiState.Loading)
            coVerify(atLeast = 2) { refreshHomeUseCase() }
        }

    @Test
    fun `trendingState should be Loading when popular movies are empty on initial load`() =
        runTest {
            every { observeTrendingMovies() } returns flowOf(emptyList())
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.trendingState.value is HomeUiState.Loading)
        }
}
