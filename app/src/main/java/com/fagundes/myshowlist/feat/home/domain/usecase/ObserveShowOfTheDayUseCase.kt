package com.fagundes.myshowlist.feat.home.domain.usecase

import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class ObserveShowOfTheDayUseCase(
    private val repository: HomeRepository,
) {
    operator fun invoke(): Flow<Movie?> = repository.observeShowOfTheDay()
}
