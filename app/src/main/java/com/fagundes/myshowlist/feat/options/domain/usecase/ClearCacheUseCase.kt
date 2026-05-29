package com.fagundes.myshowlist.feat.options.domain.usecase

import com.fagundes.myshowlist.core.data.local.dao.ContentDao
import com.fagundes.myshowlist.core.data.local.dao.MovieDetailCacheDao

class ClearCacheUseCase(
    private val contentDao: ContentDao,
    private val movieDetailCacheDao: MovieDetailCacheDao,
) {
    suspend operator fun invoke() {
        contentDao.deleteAll()
        movieDetailCacheDao.deleteAll()
    }
}
