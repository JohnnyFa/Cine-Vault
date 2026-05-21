package com.fagundes.myshowlist.feat.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fagundes.myshowlist.core.TMDB_IMAGE_BASE
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.ui.theme.AccentGold
import com.fagundes.myshowlist.ui.theme.Background
import com.fagundes.myshowlist.ui.theme.TextPrimary
import com.fagundes.myshowlist.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun ShowOfTheDaySection(
    movie: Movie,
    onMovieClick: (Movie) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onMovieClick(movie) },
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(420.dp),
        ) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data("${TMDB_IMAGE_BASE}${movie.posterUrl}")
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            // Gradient overlay
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Background.copy(alpha = 0.9f),
                                        Background,
                                    ),
                            ),
                        ),
            )

            // Conteúdo
            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(18.dp),
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = String.format(Locale.US, "%.1f", movie.rating),
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentGold,
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = movie.overview.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 4,
                )
            }
        }
    }
}
