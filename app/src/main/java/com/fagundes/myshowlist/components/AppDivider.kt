package com.fagundes.myshowlist.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = Color(0xFFE50914).copy(alpha = 0.35f),
    )
}
