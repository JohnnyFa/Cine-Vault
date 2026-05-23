package com.fagundes.myshowlist.feat.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.components.error.ErrorSection
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.ui.components.FavoritesSection
import com.fagundes.myshowlist.feat.home.ui.components.RecentsSection
import com.fagundes.myshowlist.feat.home.ui.components.RecommendedForYouSection
import com.fagundes.myshowlist.feat.home.ui.components.ShowOfTheDaySection
import com.fagundes.myshowlist.feat.home.ui.components.TrendingNowSection
import com.fagundes.myshowlist.feat.home.ui.components.skeleton.CarouselSkeleton
import com.fagundes.myshowlist.feat.home.ui.components.skeleton.ShowOfTheDaySkeleton
import com.fagundes.myshowlist.feat.home.vm.HomeUiState
import com.fagundes.myshowlist.feat.home.vm.HomeViewModel
import com.fagundes.myshowlist.ui.theme.MyShowListTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogout: () -> Unit,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    val trendingState by viewModel.trendingState.collectAsStateWithLifecycle()
    val forYouState by viewModel.forYouState.collectAsStateWithLifecycle()
    val showOfTheDayState by viewModel.showOfTheDayState.collectAsStateWithLifecycle()
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()
    val recentsState by viewModel.recentsState.collectAsStateWithLifecycle()

    HomeScreenContent(
        trendingState = trendingState,
        forYouState = forYouState,
        showOfTheDayState = showOfTheDayState,
        favoritesState = favoritesState,
        recentsState = recentsState,
        onOpenDetail = onOpenDetail,
        onLogout = onLogout,
        onRetry =
            HomeRetryActions(
                onRetryShowOfTheDay = { viewModel.loadShowOfTheDay() },
                onRetryPopular = { viewModel.loadPopular() },
                onRetryRecommended = { viewModel.loadRecommended() },
            ),
    )
}

@Composable
fun HomeScreenContent(
    trendingState: HomeUiState<List<Movie>>,
    forYouState: HomeUiState<List<Movie>>,
    showOfTheDayState: HomeUiState<Movie>,
    favoritesState: HomeUiState<List<Movie>>,
    recentsState: HomeUiState<List<Movie>>,
    onOpenDetail: (Int, ContentType) -> Unit,
    onLogout: () -> Unit,
    onRetry: HomeRetryActions,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag("home_screen_content"),
            contentPadding = WindowInsets.statusBars.asPaddingValues(),
        ) {
            showOfTheDayItem(showOfTheDayState, onRetry.onRetryShowOfTheDay, onOpenDetail)
            favoritesItem(favoritesState, onOpenDetail)
            recentsItem(recentsState, onOpenDetail)
            trendingItem(trendingState, onRetry.onRetryPopular, onOpenDetail)
            recommendedItem(forYouState, onRetry.onRetryRecommended, onOpenDetail)
            logoutItem(onLogout)
        }
    }
}

private fun LazyListScope.showOfTheDayItem(
    state: HomeUiState<Movie>,
    onRetry: () -> Unit,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    item {
        Box(modifier = Modifier.testTag("show_of_the_day_container")) {
            when (state) {
                HomeUiState.Loading -> ShowOfTheDaySkeleton()
                is HomeUiState.Success ->
                    ShowOfTheDaySection(
                        movie = state.data,
                        onMovieClick = { movie -> onOpenDetail(movie.id, movie.type) },
                    )
                is HomeUiState.Error -> ErrorSection(onRetry = onRetry)
                else -> Unit
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun LazyListScope.favoritesItem(
    state: HomeUiState<List<Movie>>,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    val movies = (state as? HomeUiState.Success)?.data ?: return
    if (movies.isEmpty()) return

    item {
        Box(modifier = Modifier.testTag("favorites_container")) {
            FavoritesSection(
                movies = movies,
                onMovieClick = { movie -> onOpenDetail(movie.id, ContentType.MOVIE) },
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun LazyListScope.recentsItem(
    state: HomeUiState<List<Movie>>,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    val movies = (state as? HomeUiState.Success)?.data ?: return
    if (movies.isEmpty()) return

    item {
        Box(modifier = Modifier.testTag("recents_container")) {
            RecentsSection(
                movies = movies,
                onMovieClick = { movie -> onOpenDetail(movie.id, movie.type) },
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun LazyListScope.trendingItem(
    state: HomeUiState<List<Movie>>,
    onRetry: () -> Unit,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    item {
        Box(modifier = Modifier.testTag("trending_now_container")) {
            when (state) {
                HomeUiState.Loading -> CarouselSkeleton()
                is HomeUiState.Success ->
                    TrendingNowSection(
                        movies = state.data,
                        onMovieClick = { movie -> onOpenDetail(movie.id, ContentType.MOVIE) },
                    )
                is HomeUiState.Error -> ErrorSection(onRetry = onRetry)
                else -> Unit
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun LazyListScope.recommendedItem(
    state: HomeUiState<List<Movie>>,
    onRetry: () -> Unit,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    item {
        Box(modifier = Modifier.testTag("for_you_container")) {
            when (state) {
                HomeUiState.Loading -> CarouselSkeleton()
                is HomeUiState.Success ->
                    RecommendedForYouSection(
                        movies = state.data,
                        onMovieClick = { movie -> onOpenDetail(movie.id, ContentType.MOVIE) },
                    )
                is HomeUiState.Error -> ErrorSection(onRetry = onRetry)
                else -> Unit
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun LazyListScope.logoutItem(onLogout: () -> Unit) {
    item {
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.testTag("logout_button"),
        ) {
            Text(stringResource(R.string.leave))
        }
    }
}

data class HomeRetryActions(
    val onRetryShowOfTheDay: () -> Unit,
    val onRetryPopular: () -> Unit,
    val onRetryRecommended: () -> Unit,
)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyShowListTheme {
        HomeScreenContent(
            trendingState =
                HomeUiState.Success(
                    listOf(
                        Movie(1, "Trending 1", null, null, 8.0),
                        Movie(2, "Trending 2", null, null, 7.5),
                    ),
                ),
            forYouState =
                HomeUiState.Success(
                    listOf(
                        Movie(3, "Recommended 1", null, null, 9.0),
                        Movie(4, "Recommended 2", null, null, 8.5),
                    ),
                ),
            showOfTheDayState =
                HomeUiState.Success(
                    Movie(5, "Show of the Day", null, "Overview", 9.5),
                ),
            favoritesState =
                HomeUiState.Success(
                    listOf(
                        Movie(1, "Favorite 1", null, null, 8.0),
                    ),
                ),
            recentsState =
                HomeUiState.Success(
                    listOf(
                        Movie(1, "Recent 1", null, null, 8.0),
                    ),
                ),
            onOpenDetail = { _, _ -> },
            onLogout = {},
            onRetry = HomeRetryActions({}, {}, {}),
        )
    }
}
