package com.fagundes.myshowlist.core.data.repository

import com.fagundes.myshowlist.core.data.local.datasource.ContentLocalDataSource
import com.fagundes.myshowlist.core.data.local.datasource.DetailCacheLocalDataSource
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

class CacheRepositoryImplTest {
    private val content: ContentLocalDataSource = mockk(relaxed = true)
    private val detailCache: DetailCacheLocalDataSource = mockk(relaxed = true)
    private val repository = CacheRepositoryImpl(content = content, detailCache = detailCache)

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `clearAll should empty both the content cache and the detail cache`() =
        runTest {
            repository.clearAll()

            coVerify(exactly = 1) { content.clearAll() }
            coVerify(exactly = 1) { detailCache.clearAll() }
        }
}
