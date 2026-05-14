package com.fagundes.myshowlist.core.data.local.entity

import androidx.room.Entity
import com.fagundes.myshowlist.core.data.local.enum.ContentType

@Entity(
    tableName = "favorites",
    primaryKeys = ["id", "type"]
)
data class FavoriteEntity(
    val id: Int,
    val type: ContentType,
    val title: String,
    val posterUrl: String?,
    val overview: String?,
    val rating: Double?,
    val favoritedAt: Long
)
