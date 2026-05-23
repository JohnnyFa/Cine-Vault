package com.fagundes.myshowlist.feat.catalog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.feat.catalog.domain.MovieGenre
import com.fagundes.myshowlist.ui.theme.CineVaultGradients
import com.fagundes.myshowlist.ui.theme.Divider
import com.fagundes.myshowlist.ui.theme.Surface
import com.fagundes.myshowlist.ui.theme.TextSecondary

@Composable
fun CategoryChipsRow(
    selectedCategory: MovieGenre,
    onCategorySelected: (MovieGenre) -> Unit,
) {
    val categories = MovieGenre.entries

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        items(categories) { genre ->
            CategoryChip(
                text = genre.displayName,
                selected = genre == selectedCategory,
                onClick = { onCategorySelected(genre) },
            )
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundModifier =
        if (selected) {
            Modifier.background(
                brush = CineVaultGradients.Brand,
                shape = CircleShape,
            )
        } else {
            Modifier.background(
                color = Surface,
                shape = CircleShape,
            )
        }

    Box(
        modifier =
            Modifier
                .height(40.dp)
                .clip(CircleShape)
                .then(backgroundModifier)
                .border(
                    width = if (selected) 0.dp else 1.dp,
                    color = Divider,
                    shape = CircleShape,
                )
                .clickable { onClick() }
                .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
