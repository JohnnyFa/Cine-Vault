package com.fagundes.myshowlist.feat.home.data.remote

import com.fagundes.myshowlist.core.data.mapper.toDomain
import com.fagundes.myshowlist.core.data.remote.api.MovieApi
import com.fagundes.myshowlist.core.domain.Movie

class HomeRemoteDataSourceImpl(
    private val movieApi: MovieApi,
) : HomeRemoteDataSource {
    override suspend fun getPopularMovies(): List<Movie> =
        movieApi.getPopularMovies()
            .results
            .map { it.toDomain() }

    override suspend fun getRecommendedMovies(): List<Movie> =
        movieApi.getRecommendedMovies()
            .results
            .map { it.toDomain() }

    override suspend fun getShowOfTheDay(): Movie =
        movieApi.getShowOfTheDay()
            .results
            .random()
            .toDomain()
}
