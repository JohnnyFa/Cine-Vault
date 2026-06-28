package com.fagundes.myshowlist.feat.catalog.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.components.EmptySection
import com.fagundes.myshowlist.components.error.ErrorSection
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.domain.MovieGenre
import com.fagundes.myshowlist.feat.catalog.ui.components.CatalogLoading
import com.fagundes.myshowlist.feat.catalog.ui.components.CatalogSearchBar
import com.fagundes.myshowlist.feat.catalog.ui.components.CategoryChipsRow
import com.fagundes.myshowlist.feat.catalog.ui.components.UpcomingHighlightCard
import com.fagundes.myshowlist.feat.catalog.ui.components.UpcomingMovieItem
import com.fagundes.myshowlist.feat.catalog.vm.CatalogNavEvent
import com.fagundes.myshowlist.feat.catalog.vm.CatalogUiState
import com.fagundes.myshowlist.feat.catalog.vm.CatalogViewModel
import com.fagundes.myshowlist.ui.theme.Background
import com.fagundes.myshowlist.ui.theme.MyShowListTheme

@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    onOpenDetail: (Int, ContentType) -> Unit,
    onSeeAllUpcoming: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            when (event) {
                CatalogNavEvent.SeeAllUpcoming -> onSeeAllUpcoming()
            }
        }
    }

    CatalogScreenContent(
        state = state,
        searchQuery = searchQuery,
        onSearchChange = viewModel::onSearchChange,
        onCategorySelected = viewModel::onCategorySelected,
        onRetry = viewModel::retry,
        onSeeAllUpcoming = viewModel::onSeeAllUpcoming,
        onOpenDetail = onOpenDetail,
    )
}

@Composable
fun CatalogScreenContent(
    state: CatalogUiState,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onCategorySelected: (MovieGenre) -> Unit,
    onRetry: () -> Unit,
    onSeeAllUpcoming: () -> Unit,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    val listState =
        rememberSaveable(
            saver = LazyListState.Saver,
        ) {
            LazyListState()
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .safeDrawingPadding(),
    ) {
        Spacer(Modifier.height(16.dp))

        CatalogHeader(
            searchQuery = searchQuery,
            selectedCategory =
                (state as? CatalogUiState.Content)?.ui?.selectedCategory
                    ?: MovieGenre.ALL,
            onSearchChange = onSearchChange,
            onCategorySelected = onCategorySelected,
        )

        Spacer(Modifier.height(24.dp))

        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn().togetherWith(fadeOut())
            },
            label = "CatalogScreenTransition",
        ) { targetState ->
            when (targetState) {
                CatalogUiState.Loading -> {
                    CatalogLoading(
                        showSearchAndCategories = false,
                        modifier = Modifier.testTag("CatalogLoading"),
                    )
                }

                is CatalogUiState.Error -> {
                    ErrorSection(
                        onRetry = onRetry,
                        modifier = Modifier.testTag("CatalogError"),
                    )
                }

                is CatalogUiState.Content -> {
                    val ui = targetState.ui

                    Column {
                        if (ui.movies.isEmpty()) {
                            EmptySection(
                                icon = painterResource(R.drawable.ic_empty_list),
                                title = "No movies found",
                                description = "Try searching for something else",
                                modifier = Modifier.testTag("CatalogEmpty"),
                            )
                        } else {
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(bottom = 120.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                modifier = Modifier.testTag("CatalogMovieList"),
                            ) {
                                item {
                                    CatalogContent(
                                        movies = ui.movies,
                                        featuredMovie = ui.featuredMovie,
                                        searchQuery = searchQuery,
                                        onSeeAllUpcoming = onSeeAllUpcoming,
                                        onOpenDetail = onOpenDetail,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogHeader(
    searchQuery: String,
    selectedCategory: MovieGenre,
    onSearchChange: (String) -> Unit,
    onCategorySelected: (MovieGenre) -> Unit,
) {
    CatalogSearchBar(
        value = searchQuery,
        onSearchChange = onSearchChange,
        modifier = Modifier.padding(horizontal = 20.dp),
    )

    Spacer(Modifier.height(16.dp))

    if (searchQuery.isBlank()) {
        CategoryChipsRow(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
        )
    }
}

@Composable
fun CatalogContent(
    movies: List<Movie>,
    featuredMovie: Movie?,
    searchQuery: String,
    onSeeAllUpcoming: () -> Unit,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    if (searchQuery.isBlank() || searchQuery.length < 2) {
        UpcomingHighlightCard(
            movie = featuredMovie,
            onSeeAll = onSeeAllUpcoming,
            onClick = { movie ->
                onOpenDetail(movie.id, ContentType.MOVIE)
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(24.dp))
    }

    Spacer(Modifier.height(24.dp))

    movies
        .filterNot { it.id == featuredMovie?.id }
        .forEach { movie ->
            UpcomingMovieItem(
                movie,
                onClick = {
                    onOpenDetail(movie.id, ContentType.MOVIE)
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenContentLoadingPreview() {
    MyShowListTheme {
        CatalogScreenContent(
            state = CatalogUiState.Loading,
            searchQuery = "",
            onSearchChange = {},
            onCategorySelected = {},
            onRetry = {},
            onSeeAllUpcoming = {},
            onOpenDetail = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenContentErrorPreview() {
    MyShowListTheme {
        CatalogScreenContent(
            state = CatalogUiState.Error("An error occurred"),
            searchQuery = "",
            onSearchChange = {},
            onCategorySelected = {},
            onRetry = {},
            onSeeAllUpcoming = {},
            onOpenDetail = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenContentEmptyPreview() {
    MyShowListTheme {
        CatalogScreenContent(
            state =
                CatalogUiState.Content(
                    com.fagundes.myshowlist.feat.catalog.vm.CatalogContentState(
                        movies = emptyList(),
                    ),
                ),
            searchQuery = "Unknown Movie",
            onSearchChange = {},
            onCategorySelected = {},
            onRetry = {},
            onSeeAllUpcoming = {},
            onOpenDetail = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenContentSuccessPreview() {
    val movies =
        listOf(
            Movie(1, "Movie 1", "/poster1.jpg", "Overview 1", 8.5),
            Movie(2, "Movie 2", "/poster2.jpg", "Overview 2", 7.0),
            Movie(3, "Movie 3", "/poster3.jpg", "Overview 3", 9.0),
        )
    MyShowListTheme {
        CatalogScreenContent(
            state =
                CatalogUiState.Content(
                    com.fagundes.myshowlist.feat.catalog.vm.CatalogContentState(
                        movies = movies,
                        featuredMovie = movies.first(),
                    ),
                ),
            searchQuery = "",
            onSearchChange = {},
            onCategorySelected = {},
            onRetry = {},
            onSeeAllUpcoming = {},
            onOpenDetail = { _, _ -> },
        )
    }
}
