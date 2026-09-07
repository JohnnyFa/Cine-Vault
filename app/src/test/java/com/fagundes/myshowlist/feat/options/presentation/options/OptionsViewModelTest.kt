package com.fagundes.myshowlist.feat.options.presentation.options

import com.fagundes.myshowlist.core.domain.AuthUser
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearCacheUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearFavoritesUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearRecentsUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearUserDataUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.GetCurrentUserUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveFavoritesCountUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveRecentsCountUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.SignOutUseCase
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OptionsViewModelTest {
    private val getCurrentUser: GetCurrentUserUseCase = mockk()
    private val signOut: SignOutUseCase = mockk(relaxed = true)
    private val clearUserDataUseCase: ClearUserDataUseCase = mockk(relaxed = true)
    private val observeFavoritesCountUseCase: ObserveFavoritesCountUseCase = mockk()
    private val observeRecentsCountUseCase: ObserveRecentsCountUseCase = mockk()
    private val clearFavoritesUseCase: ClearFavoritesUseCase = mockk(relaxed = true)
    private val clearRecentsUseCase: ClearRecentsUseCase = mockk(relaxed = true)
    private val clearCacheUseCase: ClearCacheUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getCurrentUser() } returns null
        coEvery { clearUserDataUseCase() } returns Unit
        every { observeFavoritesCountUseCase() } returns flowOf(0)
        every { observeRecentsCountUseCase() } returns flowOf(0)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() =
        OptionsViewModel(
            getCurrentUser = getCurrentUser,
            signOut = signOut,
            clearUserData = clearUserDataUseCase,
            observeFavoritesCount = observeFavoritesCountUseCase,
            observeRecentsCount = observeRecentsCountUseCase,
            clearFavorites = clearFavoritesUseCase,
            clearRecents = clearRecentsUseCase,
            clearCache = clearCacheUseCase,
        )

    @Test
    fun `state should expose the signed-in user`() {
        val user = AuthUser(displayName = "John Doe", email = "john@example.com", photoUrl = "/photo.jpg")
        every { getCurrentUser() } returns user

        val viewModel = createViewModel()

        assertEquals(user, viewModel.uiState.value.user)
    }

    @Test
    fun `state user should be null when no one is signed in`() {
        every { getCurrentUser() } returns null

        val viewModel = createViewModel()

        assertNull(viewModel.uiState.value.user)
    }

    @Test
    fun `initial state should have zero counts and no pending action`() {
        val viewModel = createViewModel()

        assertEquals(0, viewModel.uiState.value.favoritesCount)
        assertEquals(0, viewModel.uiState.value.recentsCount)
        assertNull(viewModel.uiState.value.pendingClearAction)
    }

    @Test
    fun `counts should reflect the observed use cases`() =
        runTest {
            every { observeFavoritesCountUseCase() } returns flowOf(4)
            every { observeRecentsCountUseCase() } returns flowOf(9)

            val viewModel = createViewModel()
            testDispatcher.scheduler.runCurrent()

            assertEquals(4, viewModel.uiState.value.favoritesCount)
            assertEquals(9, viewModel.uiState.value.recentsCount)
        }

    @Test
    fun `requestClear should set the pending action`() {
        val viewModel = createViewModel()

        viewModel.requestClear(ClearAction.Favorites)

        assertEquals(ClearAction.Favorites, viewModel.uiState.value.pendingClearAction)
    }

    @Test
    fun `dismissClearDialog should clear the pending action`() {
        val viewModel = createViewModel()
        viewModel.requestClear(ClearAction.Recents)

        viewModel.dismissClearDialog()

        assertNull(viewModel.uiState.value.pendingClearAction)
    }

    @Test
    fun `confirmClear should call clearFavoritesUseCase when action is Favorites`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.requestClear(ClearAction.Favorites)

            viewModel.confirmClear()
            testDispatcher.scheduler.runCurrent()

            coVerify { clearFavoritesUseCase() }
        }

    @Test
    fun `confirmClear should call clearRecentsUseCase when action is Recents`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.requestClear(ClearAction.Recents)

            viewModel.confirmClear()
            testDispatcher.scheduler.runCurrent()

            coVerify { clearRecentsUseCase() }
        }

    @Test
    fun `confirmClear should call clearCacheUseCase when action is Cache`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.requestClear(ClearAction.Cache)

            viewModel.confirmClear()
            testDispatcher.scheduler.runCurrent()

            coVerify { clearCacheUseCase() }
        }

    @Test
    fun `confirmClear should do nothing when there is no pending action`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.confirmClear()
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 0) { clearFavoritesUseCase() }
            coVerify(exactly = 0) { clearRecentsUseCase() }
            coVerify(exactly = 0) { clearCacheUseCase() }
        }

    @Test
    fun `confirmClear should clear the pending action`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.requestClear(ClearAction.Cache)

            viewModel.confirmClear()

            assertNull(viewModel.uiState.value.pendingClearAction)
        }

    @Test
    fun `logout should clear user data before signing out`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.logout()
            testDispatcher.scheduler.runCurrent()

            coVerify(ordering = Ordering.ORDERED) {
                clearUserDataUseCase()
                signOut()
            }
        }

    @Test
    fun `logout should emit LoggedOut once sign-out completes`() =
        runTest {
            val viewModel = createViewModel()

            var event: OptionsEvent? = null
            val job = launch { event = viewModel.events.first() }
            testDispatcher.scheduler.runCurrent()

            viewModel.logout()
            testDispatcher.scheduler.runCurrent()

            assertEquals(OptionsEvent.LoggedOut, event)
            job.cancel()
        }

    @Test
    fun `logout should still sign out and emit when clearing user data throws`() =
        runTest {
            coEvery { clearUserDataUseCase() } throws RuntimeException("SQLite failure")
            val viewModel = createViewModel()

            var event: OptionsEvent? = null
            val job = launch { event = viewModel.events.first() }
            testDispatcher.scheduler.runCurrent()

            viewModel.logout()
            testDispatcher.scheduler.runCurrent()

            verify { signOut() }
            assertTrue(event is OptionsEvent.LoggedOut)
            job.cancel()
        }
}
