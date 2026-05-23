package com.fagundes.myshowlist.feat.catalog.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fagundes.myshowlist.core.TMDB_IMAGE_BASE
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.ui.theme.AccentGold
import com.fagundes.myshowlist.ui.theme.SurfaceElevated

@Composable
fun UpcomingHighlightCard(
    movie: Movie?,
    onSeeAll: () -> Unit,
    onClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "FEATURED TODAY",
                color = AccentGold,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.weight(1f))

            TextButton(onClick = onSeeAll) {
                Text(text = "Ver todos", color = AccentGold)
            }
        }

        Spacer(Modifier.height(12.dp))

        if (movie == null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(SurfaceElevated),
            )
        } else {
            UpcomingMovieBanner(
                movie = movie,
                onClick = onClick,
            )
        }
    }
}

@Composable
fun UpcomingMovieBanner(
    movie: Movie,
    onClick: (Movie) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(380.dp)
                .clip(RoundedCornerShape(28.dp))
                .clickable { onClick(movie) },
    ) {
        AsyncImage(
            model = "${TMDB_IMAGE_BASE}${movie.posterUrl}",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f),
                            ),
                        ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp),
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = movie.overview.orEmpty(),
                maxLines = 3,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
            )
        }
    }
}
