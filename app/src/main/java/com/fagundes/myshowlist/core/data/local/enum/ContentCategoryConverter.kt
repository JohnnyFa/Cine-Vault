package com.fagundes.myshowlist.core.data.local.enum

import androidx.room.TypeConverter

class ContentCategoryConverter {
    @TypeConverter
    fun fromCategory(category: ContentCategory): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): ContentCategory {
        return ContentCategory.valueOf(value)
    }
}
