package com.fagundes.myshowlist.feat.login.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.ui.theme.AccentOrange

@Composable
fun LoginBackgroundDecorations() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier
                    .size(280.dp)
                    .offset(x = (-70).dp, y = (-130).dp)
                    .background(
                        color = AccentOrange.copy(alpha = 0.25f),
                        shape = CircleShape,
                    ),
        )

        Box(
            modifier =
                Modifier
                    .size(200.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 50.dp, y = 50.dp)
                    .background(
                        color = AccentOrange.copy(alpha = 0.25f),
                        shape = CircleShape,
                    ),
        )
    }
}
