package com.fagundes.myshowlist.core.domain

import com.fagundes.myshowlist.core.data.local.enum.ContentType

data class Movie(
    override val id: Int,
    override val title: String,
    override val posterUrl: String?,
    override val overview: String?,
    override val rating: Double?,
    val type: ContentType = ContentType.MOVIE,
) : Content
