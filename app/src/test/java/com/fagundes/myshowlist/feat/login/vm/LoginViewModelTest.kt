package com.fagundes.myshowlist.feat.login.vm

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val auth: FirebaseAuth = mockk(relaxed = true)
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(GoogleAuthProvider::class)
        viewModel = LoginViewModel(auth)
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
    fun `onGoogleTokenReceived should set state to Loading and then Idle on success`() =
        runTest {
            val idToken = "token"
            val credential = mockk<AuthCredential>()
            val authResultTask = mockk<Task<AuthResult>>()
            val successListenerSlot = slot<OnSuccessListener<AuthResult>>()

            every { GoogleAuthProvider.getCredential(idToken, null) } returns credential
            every { auth.signInWithCredential(credential) } returns authResultTask
            every { authResultTask.addOnSuccessListener(capture(successListenerSlot)) } returns authResultTask
            every { authResultTask.addOnFailureListener(any()) } returns authResultTask

            viewModel.onGoogleTokenReceived(idToken)

            assertEquals(LoginUiState.Loading, viewModel.uiState.value)

            successListenerSlot.captured.onSuccess(mockk())

            // Ensure state update is processed
            testDispatcher.scheduler.runCurrent()

            assertEquals(LoginUiState.Idle, viewModel.uiState.value)
            verify { auth.signInWithCredential(credential) }
        }

    @Test
    fun `onGoogleTokenReceived should set state to Error on failure`() =
        runTest {
            val idToken = "token"
            val credential = mockk<AuthCredential>()
            val authResultTask = mockk<Task<AuthResult>>()
            val failureListenerSlot = slot<OnFailureListener>()
            val errorMessage = "Firebase Error"

            every { GoogleAuthProvider.getCredential(idToken, null) } returns credential
            every { auth.signInWithCredential(credential) } returns authResultTask
            every { authResultTask.addOnSuccessListener(any()) } returns authResultTask
            every { authResultTask.addOnFailureListener(capture(failureListenerSlot)) } returns authResultTask

            viewModel.onGoogleTokenReceived(idToken)

            assertEquals(LoginUiState.Loading, viewModel.uiState.value)

            failureListenerSlot.captured.onFailure(Exception(errorMessage))

            val state = viewModel.uiState.value
            assert(state is LoginUiState.Error)
            assertEquals(errorMessage, (state as LoginUiState.Error).message)
        }
}
