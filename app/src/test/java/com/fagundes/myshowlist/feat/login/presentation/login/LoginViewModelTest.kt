package com.fagundes.myshowlist.feat.login.presentation.login

import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.feat.login.domain.usecase.ContinueAsGuestUseCase
import com.fagundes.myshowlist.feat.login.domain.usecase.LoginWithGoogleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
class LoginViewModelTest {
    private val loginWithGoogle: LoginWithGoogleUseCase = mockk()
    private val continueAsGuest: ContinueAsGuestUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginWithGoogle, continueAsGuest)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should be Idle`() =
        runTest {
            assertEquals(LoginUiState.Idle, viewModel.uiState.value)
        }

    @Test
    fun `onGoogleTokenReceived should set Loading before the sign-in completes`() =
        runTest {
            coEvery { loginWithGoogle(TOKEN) } returns Result.success(Unit)

            viewModel.onGoogleTokenReceived(TOKEN)

            assertEquals(LoginUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `onGoogleTokenReceived should return to Idle on success`() =
        runTest {
            coEvery { loginWithGoogle(TOKEN) } returns Result.success(Unit)

            viewModel.onGoogleTokenReceived(TOKEN)
            testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 1) { loginWithGoogle(TOKEN) }
            assertEquals(LoginUiState.Idle, viewModel.uiState.value)
        }

    @Test
    fun `onGoogleTokenReceived should emit NavigateHome on success`() =
        runTest {
            coEvery { loginWithGoogle(TOKEN) } returns Result.success(Unit)

            var event: LoginUiEvent? = null
            val job = launch { event = viewModel.uiEvent.first() }
            testDispatcher.scheduler.runCurrent()

            viewModel.onGoogleTokenReceived(TOKEN)
            testDispatcher.scheduler.runCurrent()

            assertEquals(LoginUiEvent.NavigateHome, event)
            job.cancel()
        }

    @Test
    fun `onGoogleTokenReceived should show a localized error instead of Firebase's message`() =
        runTest {
            coEvery { loginWithGoogle(TOKEN) } returns Result.failure(Exception("Firebase Error"))

            viewModel.onGoogleTokenReceived(TOKEN)
            testDispatcher.scheduler.runCurrent()

            assertEquals(LoginUiState.Error(R.string.authentication_failed), viewModel.uiState.value)
        }

    @Test
    fun `onGoogleTokenReceived should show the same error when the failure carries no message`() =
        runTest {
            coEvery { loginWithGoogle(TOKEN) } returns Result.failure(Exception())

            viewModel.onGoogleTokenReceived(TOKEN)
            testDispatcher.scheduler.runCurrent()

            assertEquals(LoginUiState.Error(R.string.authentication_failed), viewModel.uiState.value)
        }

    @Test
    fun `onContinueAsGuest should start a guest session and emit NavigateHome`() =
        runTest {
            var event: LoginUiEvent? = null
            val job = launch { event = viewModel.uiEvent.first() }
            testDispatcher.scheduler.runCurrent()

            viewModel.onContinueAsGuest()
            testDispatcher.scheduler.runCurrent()

            verify(exactly = 1) { continueAsGuest() }
            assertEquals(LoginUiEvent.NavigateHome, event)
            assertEquals(LoginUiState.Idle, viewModel.uiState.value)
            job.cancel()
        }

    @Test
    fun `onContinueAsGuest should be ignored while Google sign-in is pending`() =
        runTest {
            // Sign-in never completes, so the state stays Loading for the whole test.
            coEvery { loginWithGoogle(TOKEN) } coAnswers { awaitCancellation() }
            viewModel.onGoogleTokenReceived(TOKEN)

            var event: LoginUiEvent? = null
            val job = launch { event = viewModel.uiEvent.first() }
            testDispatcher.scheduler.runCurrent()

            viewModel.onContinueAsGuest()
            testDispatcher.scheduler.runCurrent()

            verify(exactly = 0) { continueAsGuest() }
            assertEquals(LoginUiState.Loading, viewModel.uiState.value)
            assertNull(event)
            job.cancel()
        }

    private companion object {
        const val TOKEN = "token"
    }
}
