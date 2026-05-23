package com.fagundes.myshowlist.core.domain

sealed interface Content {
    val id: Int
    val title: String
    val posterUrl: String?
    val overview: String?
    val rating: Double?
}
