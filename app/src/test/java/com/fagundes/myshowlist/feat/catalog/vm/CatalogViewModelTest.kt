package com.fagundes.myshowlist.feat.catalog.vm

import android.util.Log
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.data.repository.CatalogRepository
import com.fagundes.myshowlist.feat.catalog.domain.MovieGenre
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
class CatalogViewModelTest {
    private val repository: CatalogRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CatalogViewModel

    private val movies =
        listOf(
            Movie(1, "Movie 1", null, "Overview 1", 8.0),
            Movie(2, "Movie 2", null, "Overview 2", 7.5),
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        coEvery { repository.getUpcomingMovies() } returns Result.success(movies)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `init should load upcoming movies when state is Loading`() =
        runTest {
            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 1) { repository.getUpcomingMovies() }
            assertTrue(viewModel.uiState.value is CatalogUiState.Content)
            assertEquals(movies, (viewModel.uiState.value as CatalogUiState.Content).ui.movies)
        }

    @Test
    fun `retry should reload catalog`() =
        runTest {
            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.retry()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 2) { repository.getUpcomingMovies() }
        }

    @Test
    fun `onCategorySelected should load movies for category`() =
        runTest {
            val categoryMovies = listOf(Movie(3, "Action Movie", null, "Action", 9.0))
            coEvery { repository.getMoviesByCategory(any()) } returns Result.success(categoryMovies)

            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.onCategorySelected(MovieGenre.ACTION)
            testDispatcher.scheduler.runCurrent()

            coVerify { repository.getMoviesByCategory(MovieGenre.ACTION.genreId!!) }
            val state = viewModel.uiState.value as CatalogUiState.Content
            assertEquals(MovieGenre.ACTION, state.ui.selectedCategory)
            assertEquals(categoryMovies, state.ui.movies)
        }

    @Test
    fun `onCategorySelected failure should show error state`() =
        runTest {
            coEvery { repository.getMoviesByCategory(any()) } returns Result.failure(Exception("Error"))

            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.onCategorySelected(MovieGenre.ACTION)
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.uiState.value is CatalogUiState.Error)
            assertEquals("Erro ao carregar categoria", (viewModel.uiState.value as CatalogUiState.Error).message)
        }

    @Test
    fun `onCategorySelected ALL should reload catalog`() =
        runTest {
            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.onCategorySelected(MovieGenre.ALL)
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 2) { repository.getUpcomingMovies() }
        }

    @Test
    fun `onSearchChange should filter with baseMovies when query is blank`() =
        runTest {
            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.onSearchChange("")

            val state = viewModel.uiState.value as CatalogUiState.Content
            assertEquals(movies, state.ui.movies)
        }

    @Test
    fun `observeSearch should update movies when query is valid`() =
        runTest {
            val searchResults = listOf(Movie(10, "Search Result", null, "Search", 6.0))
            coEvery { repository.searchMoviesByName("Search") } returns Result.success(searchResults)

            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.onSearchChange("Search")
            testDispatcher.scheduler.advanceTimeBy(700)
            testDispatcher.scheduler.runCurrent()

            coVerify { repository.searchMoviesByName("Search") }
            val state = viewModel.uiState.value as CatalogUiState.Content
            assertEquals(searchResults, state.ui.movies)
        }

    @Test
    fun `observeSearch should show baseMovies when query length is less than 2`() =
        runTest {
            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            viewModel.onSearchChange("S")
            testDispatcher.scheduler.advanceTimeBy(700)
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value as CatalogUiState.Content
            assertEquals(movies, state.ui.movies)
        }

    @Test
    fun `loadCatalog failure should show error state`() =
        runTest {
            coEvery { repository.getUpcomingMovies() } returns Result.failure(Exception("Error"))

            viewModel = CatalogViewModel(repository)
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.uiState.value is CatalogUiState.Error)
            assertEquals("Erro ao carregar catálogo", (viewModel.uiState.value as CatalogUiState.Error).message)
        }
}
