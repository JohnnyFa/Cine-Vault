package com.fagundes.myshowlist.feat.detail.domain.model

data class ContentDetail(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val overview: String?,
    val rating: Double?,
    val type: String?,
)
