package com.fagundes.myshowlist.feat.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.ui.theme.AccentGold
import com.fagundes.myshowlist.ui.theme.TextPrimary

@Composable
fun RecommendedForYouSection(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(20.dp),
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.label_for_you),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
            )
        }

        Spacer(Modifier.height(16.dp))

        MediaCarousel(
            items = movies,
            onItemClick = onMovieClick,
        )
    }
}
