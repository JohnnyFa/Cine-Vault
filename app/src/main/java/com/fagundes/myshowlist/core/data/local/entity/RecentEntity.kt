package com.fagundes.myshowlist.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fagundes.myshowlist.core.data.local.enum.ContentType

@Entity(tableName = "recents")
data class RecentEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val type: ContentType,
    val title: String,
    val posterUrl: String?,
    val rating: Double?,
    val viewedAt: Long
)
