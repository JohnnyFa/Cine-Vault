package com.fagundes.myshowlist.feat.options.vm

import com.fagundes.myshowlist.feat.options.domain.usecase.ClearUserDataUseCase
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
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { clearUserDataUseCase() } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `currentUser should reflect the user returned by FirebaseAuth`() {
        val user: FirebaseUser = mockk()
        every { auth.currentUser } returns user

        val viewModel = OptionsViewModel(auth, clearUserDataUseCase)

        assertEquals(user, viewModel.currentUser)
    }

    @Test
    fun `currentUser should be null when no user is signed in`() {
        every { auth.currentUser } returns null

        val viewModel = OptionsViewModel(auth, clearUserDataUseCase)

        assertNull(viewModel.currentUser)
    }

    @Test
    fun `logout should clear user data before signing out`() =
        runTest {
            val viewModel = OptionsViewModel(auth, clearUserDataUseCase)
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
            val viewModel = OptionsViewModel(auth, clearUserDataUseCase)
            var callbackInvoked = false

            viewModel.logout { callbackInvoked = true }
            testDispatcher.scheduler.runCurrent()

            assert(callbackInvoked)
        }

    @Test
    fun `logout should call auth signOut`() =
        runTest {
            val viewModel = OptionsViewModel(auth, clearUserDataUseCase)

            viewModel.logout {}
            testDispatcher.scheduler.runCurrent()

            verify { auth.signOut() }
        }

    @Test
    fun `logout should still sign out and invoke onComplete when clearUserData throws`() =
        runTest {
            coEvery { clearUserDataUseCase() } throws RuntimeException("SQLite failure")
            val viewModel = OptionsViewModel(auth, clearUserDataUseCase)
            var callbackInvoked = false

            viewModel.logout { callbackInvoked = true }
            testDispatcher.scheduler.runCurrent()

            verify { auth.signOut() }
            assert(callbackInvoked)
        }
}
