package com.fagundes.myshowlist.feat.home.domain.usecase

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.data.repository.RecentRepository
import kotlinx.coroutines.flow.Flow

class ObserveRecentsUseCase(
    private val repository: RecentRepository
) {
    operator fun invoke(): Flow<List<Movie>> = repository.observeRecents()
}
