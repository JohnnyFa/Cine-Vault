package com.fagundes.myshowlist.core.domain

import com.fagundes.myshowlist.core.data.local.enum.ContentType

data class ContentItem(
    val id: Int,
    val type: ContentType,
    val title: String,
    val posterUrl: String?,
    val overview: String?,
    val rating: Double?,
)
