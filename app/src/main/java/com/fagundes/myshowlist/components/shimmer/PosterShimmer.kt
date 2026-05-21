package com.fagundes.myshowlist.components.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun PosterShimmer() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .shimmer()
                .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

fun Modifier.shimmer(): Modifier =
    composed {
        val transition = rememberInfiniteTransition(label = "shimmer")

        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "shimmerAnim",
        )

        background(
            Brush.linearGradient(
                colors =
                    listOf(
                        Color.LightGray.copy(alpha = 0.6f),
                        Color.LightGray.copy(alpha = 0.2f),
                        Color.LightGray.copy(alpha = 0.6f),
                    ),
                start = Offset(translateAnim, translateAnim),
                end = Offset(translateAnim + 200f, translateAnim + 200f),
            ),
        )
    }
