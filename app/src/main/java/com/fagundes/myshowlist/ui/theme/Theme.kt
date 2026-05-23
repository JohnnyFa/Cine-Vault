package com.fagundes.myshowlist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CineVaultColorScheme =
    darkColorScheme(
        primary = AccentGold,
        secondary = AccentOrange,
        tertiary = AccentRed,
        background = Background,
        surface = Surface,
        onPrimary = TextPrimary,
        onSecondary = TextPrimary,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
    )

@Composable
fun MyShowListTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CineVaultColorScheme,
        typography = Typography,
        content = content,
    )
}
