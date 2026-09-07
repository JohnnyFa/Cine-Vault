package com.fagundes.myshowlist.core.domain.repository

interface CacheRepository {
    suspend fun clearAll()
}
