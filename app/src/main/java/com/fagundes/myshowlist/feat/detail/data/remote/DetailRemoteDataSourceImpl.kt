package com.fagundes.myshowlist.feat.detail.data.remote

import com.fagundes.myshowlist.core.data.remote.api.MovieApi
import com.fagundes.myshowlist.core.data.remote.dto.MovieDto

class DetailRemoteDataSourceImpl(
    private val movieApi: MovieApi,
) : DetailRemoteDataSource {
    override suspend fun getContentById(id: Int): MovieDto = movieApi.getContentById(id)
}
