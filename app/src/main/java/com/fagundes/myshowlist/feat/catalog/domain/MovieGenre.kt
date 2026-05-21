package com.fagundes.myshowlist.feat.catalog.domain

enum class MovieGenre(
    val displayName: String,
    val genreId: Int?,
) {
    ALL(
        displayName = "All",
        genreId = null,
    ),

    ACTION(
        displayName = "Action",
        genreId = 28,
    ),

    ADVENTURE(
        displayName = "Adventure",
        genreId = 12,
    ),

    ANIMATION(
        displayName = "Animation",
        genreId = 16,
    ),

    COMEDY(
        displayName = "Comedy",
        genreId = 35,
    ),

    DOCUMENTARY(
        displayName = "Documentary",
        genreId = 99,
    ),

    DRAMA(
        displayName = "Drama",
        genreId = 18,
    ),

    HORROR(
        displayName = "Horror",
        genreId = 27,
    ),

    ROMANCE(
        displayName = "Romance",
        genreId = 10749,
    ),

    SCIENCE_FICTION(
        displayName = "Sci-Fi",
        genreId = 878,
    ),
}
