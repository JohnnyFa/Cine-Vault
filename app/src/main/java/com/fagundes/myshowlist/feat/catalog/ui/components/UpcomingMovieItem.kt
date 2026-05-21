package com.fagundes.myshowlist.feat.catalog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fagundes.myshowlist.core.TMDB_IMAGE_BASE
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.ui.theme.AccentGold
import com.fagundes.myshowlist.ui.theme.TextPrimary
import com.fagundes.myshowlist.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun UpcomingMovieItem(
    movie: Movie,
    onClick: (Movie) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick(movie) }
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data("${TMDB_IMAGE_BASE}${movie.posterUrl}")
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            modifier =
                Modifier
                    .width(90.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
        )

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                maxLines = 2,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "⭐ ${String.format(Locale.US, "%.1f", movie.rating)}",
                style = MaterialTheme.typography.labelMedium,
                color = AccentGold,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = movie.overview.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 3,
            )
        }
    }
}
