package com.fagundes.myshowlist.core.domain

data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val overview: String?,
    val rating: Double?,
)
