package com.fagundes.myshowlist.feat.detail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.ui.theme.RatingYellow
import com.fagundes.myshowlist.ui.theme.SurfaceElevated
import com.fagundes.myshowlist.ui.theme.TextSecondary

@Composable
fun MetaRow(
    rating: Double?,
    type: String?,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = rating?.let { "⭐ ${String.format("%.1f", it)}" } ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            color = RatingYellow,
        )

        if (!type.isNullOrBlank()) {
            Spacer(Modifier.width(12.dp))

            Box(
                modifier =
                    Modifier
                        .background(
                            color = SurfaceElevated,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = type.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
            }
        }
    }
}
