package com.fagundes.myshowlist.components.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerBox(
    width: Dp,
    height: Dp,
    radius: Dp = 8.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(width, height)
                .background(
                    brush = shimmerBrush(),
                    shape = RoundedCornerShape(radius),
                ),
    )
}
