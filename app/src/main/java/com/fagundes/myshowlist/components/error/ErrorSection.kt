package com.fagundes.myshowlist.components.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.ui.theme.CineVaultGradients
import com.fagundes.myshowlist.ui.theme.NeonRed
import com.fagundes.myshowlist.ui.theme.TextMuted
import com.fagundes.myshowlist.ui.theme.TextPrimary

@Composable
fun ErrorSection(
    onRetry: (() -> Unit),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .testTag("error_section"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = NeonRed,
            modifier = Modifier.size(64.dp),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Oops! Something went wrong",
            color = TextPrimary,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "We’re having trouble loading movies right now.",
            color = TextMuted,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                ),
            contentPadding = PaddingValues(),
            modifier =
                Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CineVaultGradients.Brand)
                    .testTag("error_retry_button"),
        ) {
            Text(
                text = "Retry",
                color = Color.Black,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
