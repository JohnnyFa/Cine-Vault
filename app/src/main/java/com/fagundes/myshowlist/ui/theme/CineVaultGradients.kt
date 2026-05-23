package com.fagundes.myshowlist.ui.theme

import androidx.compose.ui.graphics.Brush

object CineVaultGradients {
    val Brand =
        Brush.Companion.linearGradient(
            colors =
                listOf(
                    AccentGold,
                    AccentOrange,
                    AccentRed,
                ),
        )

    val SubtleBackground =
        Brush.Companion.radialGradient(
            colors =
                listOf(
                    AccentGold.copy(alpha = 0.08f),
                    Background,
                ),
        )
}
