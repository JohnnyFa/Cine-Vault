package com.fagundes.myshowlist.core.data.local.enum

import androidx.room.TypeConverter

class ContentTypeConverter {
    @TypeConverter
    fun fromContentType(type: ContentType): String = type.name

    @TypeConverter
    fun toContentType(value: String): ContentType = ContentType.valueOf(value)
}
