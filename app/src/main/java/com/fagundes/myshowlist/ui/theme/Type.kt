package com.fagundes.myshowlist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fagundes.myshowlist.R

val DmSerifDisplay =
    FontFamily(
        Font(
            resId = R.font.dm_serif_display_regular,
            weight = FontWeight.Normal,
        ),
    )

val Outfit =
    FontFamily(
        Font(R.font.outfit_regular, FontWeight.Normal),
        Font(R.font.outfit_medium, FontWeight.Medium),
        Font(R.font.outfit_semibold, FontWeight.SemiBold),
        Font(R.font.outfit_bold, FontWeight.Bold),
    )

val Typography =
    Typography(
    /* ---------------------------
       BRAND / HEADERS
       --------------------------- */
        displayLarge =
            TextStyle(
                fontFamily = DmSerifDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 42.sp,
                lineHeight = 46.sp,
                letterSpacing = 1.sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = DmSerifDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                letterSpacing = 0.8.sp,
            ),
            /* ---------------------------
       UI TITLES
       --------------------------- */
        titleLarge =
            TextStyle(
                fontFamily = Outfit,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 24.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = Outfit,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 20.sp,
            ),
            /* ---------------------------
       BODY / TEXT
       --------------------------- */
        bodyLarge =
            TextStyle(
                fontFamily = Outfit,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 22.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = Outfit,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = Outfit,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            ),
                /* ---------------------------
       LABELS / BUTTONS
       --------------------------- */
        labelLarge =
            TextStyle(
                fontFamily = Outfit,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = Outfit,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            ),
    )
