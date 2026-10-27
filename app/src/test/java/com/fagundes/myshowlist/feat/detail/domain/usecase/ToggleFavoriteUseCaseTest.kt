package com.fagundes.myshowlist.feat.detail.domain.usecase

import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.AuthSession
import com.fagundes.myshowlist.core.domain.AuthUser
import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.core.domain.SignInRequiredException
import com.fagundes.myshowlist.core.domain.repository.AuthRepository
import com.fagundes.myshowlist.feat.detail.domain.repository.DetailRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleFavoriteUseCaseTest {
    private val repository: DetailRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val useCase = ToggleFavoriteUseCase(repository, authRepository)

    private val item =
        ContentItem(
            id = 1,
            type = ContentType.MOVIE,
            title = "Movie 1",
            posterUrl = "/poster.jpg",
            overview = "Overview 1",
            rating = 8.0,
        )

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke should toggle the favorite when signed in`() =
        runTest {
            every { authRepository.currentSession() } returns AuthSession.SignedIn(AuthUser("John", "john@example.com", null))
            coEvery { repository.toggleFavorite(item) } returns Result.success(true)

            val result = useCase(item)

            coVerify(exactly = 1) { repository.toggleFavorite(item) }
            assertEquals(true, result.getOrNull())
        }

    @Test
    fun `invoke should fail with SignInRequiredException for a guest without touching the repository`() =
        runTest {
            every { authRepository.currentSession() } returns AuthSession.Guest

            val result = useCase(item)

            assertTrue(result.exceptionOrNull() is SignInRequiredException)
            coVerify(exactly = 0) { repository.toggleFavorite(any()) }
        }

    @Test
    fun `invoke should fail with SignInRequiredException when signed out`() =
        runTest {
            every { authRepository.currentSession() } returns AuthSession.SignedOut

            val result = useCase(item)

            assertTrue(result.exceptionOrNull() is SignInRequiredException)
            coVerify(exactly = 0) { repository.toggleFavorite(any()) }
        }
}
