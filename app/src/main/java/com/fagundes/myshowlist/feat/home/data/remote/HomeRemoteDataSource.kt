package com.fagundes.myshowlist.feat.home.data.remote

import com.fagundes.myshowlist.core.domain.Movie

interface HomeRemoteDataSource {
    suspend fun getPopularMovies(): List<Movie>

    suspend fun getRecommendedMovies(): List<Movie>

    suspend fun getShowOfTheDay(): Movie
}
