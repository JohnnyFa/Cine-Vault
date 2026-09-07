package com.fagundes.myshowlist.feat.catalog.presentation.catalog

import android.util.Log
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.domain.model.MovieGenre
import com.fagundes.myshowlist.feat.catalog.domain.usecase.GetMoviesByGenreUseCase
import com.fagundes.myshowlist.feat.catalog.domain.usecase.GetUpcomingMoviesUseCase
import com.fagundes.myshowlist.feat.catalog.domain.usecase.SearchMoviesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
class CatalogViewModelTest {
    private val getUpcomingMovies: GetUpcomingMoviesUseCase = mockk(relaxed = true)
    private val getMoviesByGenre: GetMoviesByGenreUseCase = mockk(relaxed = true)
    private val searchMovies: SearchMoviesUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CatalogViewModel

    private val movies =
        listOf(
            Movie(1, "Movie 1", null, "Overview 1", 8.0),
            Movie(2, "Movie 2", null, "Overview 2", 7.5),
        )

    private fun buildViewModel() = CatalogViewModel(getUpcomingMovies, getMoviesByGenre, searchMovies)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        coEvery { getUpcomingMovies() } returns Result.success(movies)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `init should load upcoming movies when state is Loading`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 1) { getUpcomingMovies() }
            assertTrue(viewModel.uiState.value is CatalogUiState.Content)
            assertEquals(movies, (viewModel.uiState.value as CatalogUiState.Content).ui.movies)
        }

    @Test
    fun `retry should reload catalog`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.retry()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 2) { getUpcomingMovies() }
        }

    @Test
    fun `onCategorySelected should load movies for genre`() =
        runTest {
            val genreMovies = listOf(Movie(3, "Action Movie", null, "Action", 9.0))
            coEvery { getMoviesByGenre(any()) } returns Result.success(genreMovies)

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.onCategorySelected(MovieGenre.ACTION)
            testDispatcher.scheduler.runCurrent()

            coVerify { getMoviesByGenre(MovieGenre.ACTION) }
            val state = viewModel.uiState.value as CatalogUiState.Content
            assertEquals(MovieGenre.ACTION, state.ui.selectedCategory)
            assertEquals(genreMovies, state.ui.movies)
        }

    @Test
    fun `onCategorySelected failure should show error state`() =
        runTest {
            coEvery { getMoviesByGenre(any()) } returns Result.failure(Exception("Error"))

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.onCategorySelected(MovieGenre.ACTION)
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.uiState.value is CatalogUiState.Error)
            assertEquals("Erro ao carregar categoria", (viewModel.uiState.value as CatalogUiState.Error).message)
        }

    @Test
    fun `onCategorySelected ALL should reload catalog`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.onCategorySelected(MovieGenre.ALL)
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 2) { getUpcomingMovies() }
            coVerify(exactly = 0) { getMoviesByGenre(any()) }
        }

    @Test
    fun `onSearchChange should filter with baseMovies when query is blank`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.onSearchChange("")

            val state = viewModel.uiState.value as CatalogUiState.Content
            assertEquals(movies, state.ui.movies)
        }

    @Test
    fun `observeSearch should update movies when query is valid`() =
        runTest {
            val searchResults = listOf(Movie(10, "Search Result", null, "Search", 6.0))
            coEvery { searchMovies("Search") } returns Result.success(searchResults)

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.onSearchChange("Search")
            testDispatcher.scheduler.advanceTimeBy(700)
            testDispatcher.scheduler.runCurrent()

            coVerify { searchMovies("Search") }
            val state = viewModel.uiState.value as CatalogUiState.Content
            assertEquals(searchResults, state.ui.movies)
        }

    @Test
    fun `observeSearch should show baseMovies when query length is less than 2`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.onSearchChange("S")
            testDispatcher.scheduler.advanceTimeBy(700)
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value as CatalogUiState.Content
            assertEquals(movies, state.ui.movies)
            coVerify(exactly = 0) { searchMovies(any()) }
        }

    @Test
    fun `loadCatalog failure should show error state`() =
        runTest {
            coEvery { getUpcomingMovies() } returns Result.failure(Exception("Error"))

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.uiState.value is CatalogUiState.Error)
            assertEquals("Erro ao carregar catálogo", (viewModel.uiState.value as CatalogUiState.Error).message)
        }

    @Test
    fun `onSeeAllUpcoming should emit SeeAllUpcoming nav event`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            var emittedEvent: CatalogNavEvent? = null
            val job = launch { emittedEvent = viewModel.navEvent.first() }

            viewModel.onSeeAllUpcoming()
            testDispatcher.scheduler.runCurrent()

            assertEquals(CatalogNavEvent.SeeAllUpcoming, emittedEvent)
            job.cancel()
        }
}
