package com.fagundes.myshowlist.feat.detail.domain

data class ContentDetailUi(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val overview: String?,
    val rating: Double?,
    val type: String?,
)
