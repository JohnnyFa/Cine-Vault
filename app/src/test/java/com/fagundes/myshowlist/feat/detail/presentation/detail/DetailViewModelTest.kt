package com.fagundes.myshowlist.feat.detail.presentation.detail

import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.core.domain.SignInRequiredException
import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveContentDetailUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveFavoriteStateUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.RefreshContentDetailUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.SaveRecentMovieUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val observeContentDetail: ObserveContentDetailUseCase = mockk()
    private val refreshContentDetail: RefreshContentDetailUseCase = mockk()
    private val observeFavoriteState: ObserveFavoriteStateUseCase = mockk()
    private val toggleFavorite: ToggleFavoriteUseCase = mockk()
    private val saveRecentMovie: SaveRecentMovieUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DetailViewModel

    private val detail =
        ContentDetail(
            id = ID,
            title = "Movie 1",
            imageUrl = "/poster.jpg",
            overview = "Overview 1",
            rating = 8.0,
            type = TYPE.name,
        )

    private val expectedItem =
        ContentItem(
            id = ID,
            type = TYPE,
            title = "Movie 1",
            posterUrl = "/poster.jpg",
            overview = "Overview 1",
            rating = 8.0,
        )

    private fun buildViewModel() =
        DetailViewModel(
            id = ID,
            type = TYPE,
            observeContentDetail = observeContentDetail,
            refreshContentDetail = refreshContentDetail,
            observeFavoriteState = observeFavoriteState,
            toggleFavorite = toggleFavorite,
            saveRecentMovie = saveRecentMovie,
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // The init block collects eagerly — every collaborator must be stubbed before construction.
        every { observeContentDetail(ID, TYPE) } returns flowOf(detail)
        every { observeFavoriteState(ID, TYPE) } returns flowOf(false)
        coEvery { refreshContentDetail(ID) } returns Result.success(Unit)
        coEvery { toggleFavorite(any()) } returns Result.success(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state is Loading before the detail flow emits`() =
        runTest {
            every { observeContentDetail(ID, TYPE) } returns flowOf()

            viewModel = buildViewModel()

            assertEquals(DetailUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `init should expose Success once the detail flow emits`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value
            assertTrue(state is DetailUiState.Success)
            assertEquals(detail, (state as DetailUiState.Success).ui)
        }

    @Test
    fun `init should record the item as recently viewed`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 1) { saveRecentMovie(expectedItem) }
        }

    @Test
    fun `init should refresh the detail`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 1) { refreshContentDetail(ID) }
        }

    @Test
    fun `refresh failure should show Error while still Loading`() =
        runTest {
            every { observeContentDetail(ID, TYPE) } returns flowOf()
            coEvery { refreshContentDetail(ID) } returns Result.failure(Exception("Network error"))

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value
            assertTrue(state is DetailUiState.Error)
            assertEquals(R.string.error_load_detail, (state as DetailUiState.Error).message)
        }

    @Test
    fun `refresh failure should not overwrite an already loaded detail`() =
        runTest {
            coEvery { refreshContentDetail(ID) } returns Result.failure(Exception("Network error"))

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.uiState.value is DetailUiState.Success)
        }

    @Test
    fun `favorite state from the repository should be reflected in Success`() =
        runTest {
            every { observeFavoriteState(ID, TYPE) } returns flowOf(true)

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            assertTrue((viewModel.uiState.value as DetailUiState.Success).isFavorite)
        }

    @Test
    fun `onFavoriteClick should pass the loaded item to toggleFavorite`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.onFavoriteClick()
            testDispatcher.scheduler.runCurrent()

            // The item travels with the call; the repository holds no candidate state of its own.
            coVerify(exactly = 1) { toggleFavorite(expectedItem) }
        }

    @Test
    fun `onFavoriteClick success should update state and emit FavoriteUpdated`() =
        runTest {
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            var event: DetailEvent? = null
            val job = launch { event = viewModel.events.first() }
            testDispatcher.scheduler.runCurrent()

            viewModel.onFavoriteClick()
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value as DetailUiState.Success
            assertTrue(state.isFavorite)
            assertFalse(state.isFavoriteLoading)
            assertEquals(DetailEvent.FavoriteUpdated(true), event)
            job.cancel()
        }

    @Test
    fun `onFavoriteClick failure should clear loading and emit ShowError`() =
        runTest {
            coEvery { toggleFavorite(any()) } returns Result.failure(Exception("DB error"))

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            var event: DetailEvent? = null
            val job = launch { event = viewModel.events.first() }
            testDispatcher.scheduler.runCurrent()

            viewModel.onFavoriteClick()
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value as DetailUiState.Success
            assertFalse(state.isFavoriteLoading)
            assertEquals(DetailEvent.ShowError(R.string.error_update_favorite), event)
            job.cancel()
        }

    @Test
    fun `onFavoriteClick for a guest should clear loading and emit RequireSignIn`() =
        runTest {
            coEvery { toggleFavorite(any()) } returns Result.failure(SignInRequiredException())

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            var event: DetailEvent? = null
            val job = launch { event = viewModel.events.first() }
            testDispatcher.scheduler.runCurrent()

            viewModel.onFavoriteClick()
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value as DetailUiState.Success
            assertFalse(state.isFavoriteLoading)
            assertFalse(state.isFavorite)
            assertEquals(DetailEvent.RequireSignIn, event)
            job.cancel()
        }

    @Test
    fun `onFavoriteClick should be ignored while the detail is still loading`() =
        runTest {
            every { observeContentDetail(ID, TYPE) } returns flowOf()

            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.onFavoriteClick()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 0) { toggleFavorite(any()) }
        }

    @Test
    fun `retry should go back to Loading and refresh again`() =
        runTest {
            every { observeContentDetail(ID, TYPE) } returns flowOf()
            coEvery { refreshContentDetail(ID) } returns Result.failure(Exception("Network error"))
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()
            assertTrue(viewModel.uiState.value is DetailUiState.Error)

            coEvery { refreshContentDetail(ID) } returns Result.success(Unit)
            viewModel.retry()

            assertEquals(DetailUiState.Loading, viewModel.uiState.value)
            testDispatcher.scheduler.runCurrent()
            coVerify(exactly = 2) { refreshContentDetail(ID) }
        }

    @Test
    fun `retry that fails again should show Error again`() =
        runTest {
            every { observeContentDetail(ID, TYPE) } returns flowOf()
            coEvery { refreshContentDetail(ID) } returns Result.failure(Exception("Network error"))
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.retry()
            testDispatcher.scheduler.runCurrent()

            assertEquals(DetailUiState.Error(R.string.error_load_detail), viewModel.uiState.value)
        }

    @Test
    fun `retry should not start a second refresh while one is in flight`() =
        runTest {
            every { observeContentDetail(ID, TYPE) } returns flowOf()
            coEvery { refreshContentDetail(ID) } coAnswers {
                delay(1_000)
                Result.failure(Exception("Network error"))
            }
            viewModel = buildViewModel()
            testDispatcher.scheduler.runCurrent()

            viewModel.retry()
            viewModel.retry()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 1) { refreshContentDetail(ID) }
        }

    private companion object {
        const val ID = 1
        val TYPE = ContentType.MOVIE
    }
}
