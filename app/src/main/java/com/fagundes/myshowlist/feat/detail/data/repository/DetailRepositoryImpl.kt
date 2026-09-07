package com.fagundes.myshowlist.feat.detail.data.repository

import com.fagundes.myshowlist.core.CACHE_DURATION
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.ContentItem
import com.fagundes.myshowlist.feat.detail.data.local.DetailLocalDataSource
import com.fagundes.myshowlist.feat.detail.data.mapper.toCachedEntity
import com.fagundes.myshowlist.feat.detail.data.mapper.toContentDetail
import com.fagundes.myshowlist.feat.detail.data.mapper.toFavoriteEntity
import com.fagundes.myshowlist.feat.detail.data.remote.DetailRemoteDataSource
import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail
import com.fagundes.myshowlist.feat.detail.domain.repository.DetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DetailRepositoryImpl(
    private val remote: DetailRemoteDataSource,
    private val local: DetailLocalDataSource,
) : DetailRepository {
    override fun observeContentDetail(
        id: Int,
        type: ContentType,
    ): Flow<ContentDetail?> =
        local.observeDetail(id)
            .map { cached -> cached?.toContentDetail(type) }
            .distinctUntilChanged()

    override suspend fun refreshDetailIfNeeded(id: Int): Result<Unit> =
        runCatching {
            val now = System.currentTimeMillis()
            val cached = local.getDetail(id)
            val expired = cached == null || (now - cached.cachedAt) > CACHE_DURATION
            if (!expired) return@runCatching

            local.saveDetail(remote.getContentById(id).toCachedEntity(cachedAt = now))
            local.clearExpiredDetails(now - CACHE_DURATION)
        }

    override fun observeFavoriteState(
        id: Int,
        type: ContentType,
    ): Flow<Boolean> = local.observeFavorite(id, type).map { it != null }

    override suspend fun toggleFavorite(item: ContentItem): Result<Boolean> =
        runCatching {
            if (local.isFavorite(item.id, item.type)) {
                local.removeFavorite(item.id, item.type)
                false
            } else {
                local.addFavorite(item.toFavoriteEntity(favoritedAt = System.currentTimeMillis()))
                true
            }
        }
}
