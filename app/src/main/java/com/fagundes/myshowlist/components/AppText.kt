package com.fagundes.myshowlist.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 14.sp,
    weight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colorScheme.onSurface,
    align: TextAlign = TextAlign.Center,
    letterSpacing: TextUnit = 0.sp,
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = align,
        color = color,
        fontSize = size,
        fontWeight = weight,
        letterSpacing = letterSpacing,
    )
}

@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
) {
    AppText(
        text = text,
        size = 28.sp,
        weight = FontWeight.ExtraBold,
        letterSpacing = 2.sp,
        modifier = modifier,
    )
}

@Composable
fun NormalText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 14.sp,
    weight: FontWeight = FontWeight.Normal,
    align: TextAlign = TextAlign.Start,
    letterSpacing: TextUnit = 0.sp,
) {
    AppText(
        text = text,
        size = size,
        weight = weight,
        letterSpacing = letterSpacing,
        modifier = modifier,
        align = align,
    )
}

@Composable
fun SubtitleText(
    text: String,
    modifier: Modifier = Modifier,
) {
    AppText(
        text = text,
        size = 16.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
        modifier = modifier,
    )
}

@Composable
fun CaptionText(
    text: String,
    modifier: Modifier = Modifier,
) {
    AppText(
        text = text,
        size = 10.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        modifier = modifier,
    )
}

@Composable
fun ErrorText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 14.sp,
    weight: FontWeight = FontWeight.Normal,
) {
    AppText(
        text = text,
        size = size,
        weight = weight,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier,
    )
}
