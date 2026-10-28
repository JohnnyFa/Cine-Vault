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

    override suspend fun getTopRatedMovies(): List<Movie> =
        movieApi.getTopRatedMovies()
            .results
            .map { it.toDomain() }

    override suspend fun getRecommendationsFor(movieId: Int): List<Movie> =
        movieApi.getRecommendationsFor(movieId)
            .results
            .map { it.toDomain() }

    override suspend fun getShowOfTheDay(): Movie =
        movieApi.getShowOfTheDay()
            .results
            .random()
            .toDomain()
}
