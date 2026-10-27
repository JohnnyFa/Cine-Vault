package com.fagundes.myshowlist.feat.detail.domain.usecase

import com.fagundes.myshowlist.core.domain.AuthSession
import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.core.domain.SignInRequiredException
import com.fagundes.myshowlist.core.domain.repository.AuthRepository
import com.fagundes.myshowlist.feat.detail.domain.repository.DetailRepository

class ToggleFavoriteUseCase(
    private val repository: DetailRepository,
    private val authRepository: AuthRepository,
) {
    /**
     * Takes the item to favourite so the repository holds no per-item state between calls.
     * Favourites need an account, so guests get [SignInRequiredException] and nothing is written.
     */
    suspend operator fun invoke(item: ContentItem): Result<Boolean> =
        if (authRepository.currentSession() is AuthSession.SignedIn) {
            repository.toggleFavorite(item)
        } else {
            Result.failure(SignInRequiredException())
        }
}
