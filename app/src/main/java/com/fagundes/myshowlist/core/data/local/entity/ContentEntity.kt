package com.fagundes.myshowlist.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.fagundes.myshowlist.core.data.local.enum.ContentCategory
import com.fagundes.myshowlist.core.data.local.enum.ContentType

@Entity(
    tableName = "content",
    primaryKeys = ["id", "category"],
    indices = [Index(value = ["category"]), Index(value = ["id"])],
)
data class ContentEntity(
    val id: Int,
    val type: ContentType,
    val title: String,
    val posterUrl: String?,
    val backdropPath: String?,
    val overview: String?,
    val rating: Double?,
    val releaseDate: String?,
    val category: ContentCategory,
    val cachedAt: Long,
)
