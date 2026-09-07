package com.fagundes.myshowlist.feat.login.presentation.login

import com.fagundes.myshowlist.feat.login.domain.usecase.LoginWithGoogleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
class LoginViewModelTest {
    private val loginWithGoogle: LoginWithGoogleUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginWithGoogle)
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
    fun `onGoogleTokenReceived should surface the failure message`() =
        runTest {
            coEvery { loginWithGoogle(TOKEN) } returns Result.failure(Exception("Firebase Error"))

            viewModel.onGoogleTokenReceived(TOKEN)
            testDispatcher.scheduler.runCurrent()

            val state = viewModel.uiState.value
            assertTrue(state is LoginUiState.Error)
            assertEquals("Firebase Error", (state as LoginUiState.Error).message)
        }

    @Test
    fun `onGoogleTokenReceived should fall back when the failure carries no message`() =
        runTest {
            coEvery { loginWithGoogle(TOKEN) } returns Result.failure(Exception())

            viewModel.onGoogleTokenReceived(TOKEN)
            testDispatcher.scheduler.runCurrent()

            assertEquals("Erro Firebase", (viewModel.uiState.value as LoginUiState.Error).message)
        }

    private companion object {
        const val TOKEN = "token"
    }
}
