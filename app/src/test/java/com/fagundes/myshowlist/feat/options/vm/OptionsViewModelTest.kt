package com.fagundes.myshowlist.feat.options.vm

import com.fagundes.myshowlist.feat.options.domain.usecase.ClearCacheUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearFavoritesUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearRecentsUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearUserDataUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveFavoritesCountUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveRecentsCountUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OptionsViewModelTest {
    private val auth: FirebaseAuth = mockk(relaxed = true)
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
            auth = auth,
            clearUserDataUseCase = clearUserDataUseCase,
            observeFavoritesCountUseCase = observeFavoritesCountUseCase,
            observeRecentsCountUseCase = observeRecentsCountUseCase,
            clearFavoritesUseCase = clearFavoritesUseCase,
            clearRecentsUseCase = clearRecentsUseCase,
            clearCacheUseCase = clearCacheUseCase,
        )

    @Test
    fun `currentUser should reflect the user returned by FirebaseAuth`() {
        val user: FirebaseUser = mockk()
        every { auth.currentUser } returns user

        val viewModel = createViewModel()

        assertEquals(user, viewModel.currentUser)
    }

    @Test
    fun `currentUser should be null when no user is signed in`() {
        every { auth.currentUser } returns null

        val viewModel = createViewModel()

        assertNull(viewModel.currentUser)
    }

    @Test
    fun `initial state should have zero counts and no pending action`() {
        val viewModel = createViewModel()

        assertEquals(0, viewModel.uiState.value.favoritesCount)
        assertEquals(0, viewModel.uiState.value.recentsCount)
        assertNull(viewModel.uiState.value.pendingClearAction)
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
            var callbackInvoked = false

            viewModel.logout { callbackInvoked = true }
            testDispatcher.scheduler.runCurrent()

            coVerify(ordering = io.mockk.Ordering.ORDERED) {
                clearUserDataUseCase()
                auth.signOut()
            }
            assert(callbackInvoked)
        }

    @Test
    fun `logout should invoke onComplete callback after clearing data`() =
        runTest {
            val viewModel = createViewModel()
            var callbackInvoked = false

            viewModel.logout { callbackInvoked = true }
            testDispatcher.scheduler.runCurrent()

            assert(callbackInvoked)
        }

    @Test
    fun `logout should call auth signOut`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.logout {}
            testDispatcher.scheduler.runCurrent()

            verify { auth.signOut() }
        }

    @Test
    fun `logout should still sign out and invoke onComplete when clearUserData throws`() =
        runTest {
            coEvery { clearUserDataUseCase() } throws RuntimeException("SQLite failure")
            val viewModel = createViewModel()
            var callbackInvoked = false

            viewModel.logout { callbackInvoked = true }
            testDispatcher.scheduler.runCurrent()

            verify { auth.signOut() }
            assert(callbackInvoked)
        }
}
