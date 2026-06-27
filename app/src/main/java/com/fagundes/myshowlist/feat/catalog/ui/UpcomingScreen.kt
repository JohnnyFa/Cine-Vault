package com.fagundes.myshowlist.feat.catalog.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.components.EmptySection
import com.fagundes.myshowlist.components.error.ErrorSection
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.catalog.ui.components.CatalogLoading
import com.fagundes.myshowlist.feat.catalog.ui.components.UpcomingMovieItem
import com.fagundes.myshowlist.feat.catalog.vm.UpcomingUiState
import com.fagundes.myshowlist.feat.catalog.vm.UpcomingViewModel
import com.fagundes.myshowlist.ui.theme.Background
import com.fagundes.myshowlist.ui.theme.TextPrimary

@Composable
fun UpcomingScreen(
    viewModel: UpcomingViewModel,
    onBack: () -> Unit,
    onOpenDetail: (Int, ContentType) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .safeDrawingPadding(),
    ) {
        UpcomingTopBar(onBack = onBack)

        AnimatedContent(
            targetState = state,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            label = "UpcomingTransition",
        ) { targetState ->
            when (targetState) {
                UpcomingUiState.Loading -> {
                    CatalogLoading(showSearchAndCategories = false)
                }

                is UpcomingUiState.Error -> {
                    ErrorSection(onRetry = viewModel::retry)
                }

                is UpcomingUiState.Content -> {
                    val movies = targetState.movies
                    if (movies.isEmpty()) {
                        EmptySection(
                            icon = painterResource(R.drawable.ic_empty_list),
                            title = "No upcoming movies",
                            description = "Check back later for new releases",
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            items(movies) { movie ->
                                UpcomingMovieItem(
                                    movie = movie,
                                    onClick = { onOpenDetail(movie.id, ContentType.MOVIE) },
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingTopBar(onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary,
            modifier =
                Modifier
                    .size(40.dp)
                    .clickable(onClick = onBack)
                    .padding(8.dp),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Upcoming Movies",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )
    }
}
