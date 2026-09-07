package com.fagundes.myshowlist.feat.detail.data.remote

import com.fagundes.myshowlist.core.data.remote.dto.MovieDto

interface DetailRemoteDataSource {
    suspend fun getContentById(id: Int): MovieDto
}
