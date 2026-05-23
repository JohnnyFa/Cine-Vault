package com.fagundes.myshowlist.feat.detail.domain

import com.fagundes.myshowlist.core.data.local.enum.ContentType

data class FavoriteItem(
    val id: Int,
    val type: ContentType,
    val title: String,
    val posterUrl: String?,
    val overview: String?,
    val rating: Double?,
)
