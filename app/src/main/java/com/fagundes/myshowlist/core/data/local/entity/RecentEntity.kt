package com.fagundes.myshowlist.core.data.local.entity

import androidx.room.Entity
import com.fagundes.myshowlist.core.data.local.enum.ContentType

@Entity(
    tableName = "recents",
    primaryKeys = ["id", "type"]
)
data class RecentEntity(
    val id: Int,
    val type: ContentType,
    val title: String,
    val posterUrl: String?,
    val rating: Double?,
    val viewedAt: Long
)
