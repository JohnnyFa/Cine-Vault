package com.fagundes.myshowlist.core.data.remote.api

import com.fagundes.myshowlist.core.data.remote.dto.MovieDto
import com.fagundes.myshowlist.core.data.remote.response.TmdbResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovieApi(
    private val client: HttpClient,
) {
    suspend fun getPopularMovies(): TmdbResponse = client.get("movie/popular") {}.body()

    suspend fun getRecommendedMovies(): TmdbResponse = client.get("movie/upcoming").body()

    suspend fun getShowOfTheDay(): TmdbResponse = client.get("discover/movie").body()

    suspend fun getUpcomingMovies(): TmdbResponse = client.get("movie/upcoming").body()

    suspend fun getMoviesByCategory(category: Int): TmdbResponse {
        return client
            .get("discover/movie") { parameter("with_genres", category) }
            .body()
    }

    suspend fun getMoviesByName(name: String): TmdbResponse {
        return client
            .get("search/movie") { parameter("query", name) }.body()
    }

    suspend fun getContentById(id: Int): MovieDto {
        return client
            .get("movie/$id").body()
    }
}
