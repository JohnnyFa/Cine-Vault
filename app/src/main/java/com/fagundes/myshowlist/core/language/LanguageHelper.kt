package com.fagundes.myshowlist.core.language

import java.util.Locale

fun currentTmdbLanguage(): String {
    val locale = Locale.getDefault()

    return when (locale.language) {
        "pt" -> "pt-BR"
        "en" -> "en-US"
        else -> "en-US"
    }
}
