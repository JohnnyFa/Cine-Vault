package com.fagundes.myshowlist.feat.home.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import java.util.Locale

@Composable
fun MediaCarousel(
    items: List<Movie>,
    onItemClick: (Movie) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items.size) { index ->
            MediaCard(
                movie = items[index],
                onClick = { onItemClick(items[index]) },
            )
        }
    }
}

@Composable
fun MediaCard(
    movie: Movie,
    onClick: () -> Unit,
) {
    val formattedRating = String.format(Locale.US, "%.1f", movie.rating)

    Column(
        modifier =
            Modifier
                .width(140.dp)
                .clickable { onClick() },
    ) {
        Box(
            modifier =
                Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
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
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = movie.title,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(14.dp),
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = formattedRating,
                style = MaterialTheme.typography.labelSmall,
                color = AccentGold,
            )
        }
    }
}
