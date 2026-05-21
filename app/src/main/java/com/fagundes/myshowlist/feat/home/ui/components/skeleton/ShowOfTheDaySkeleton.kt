package com.fagundes.myshowlist.feat.home.ui.components.skeleton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.components.shimmer.ShimmerBox

@Composable
fun ShowOfTheDaySkeleton() {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(420.dp),
        ) {
            ShimmerBox(
                width = 500.dp,
                height = 420.dp,
                radius = 0.dp,
            )

            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShimmerBox(width = 18.dp, height = 18.dp, radius = 4.dp)
                    Spacer(Modifier.width(6.dp))
                    ShimmerBox(width = 30.dp, height = 16.dp, radius = 4.dp)
                }

                Spacer(Modifier.height(12.dp))

                ShimmerBox(width = 240.dp, height = 32.dp, radius = 4.dp)

                Spacer(Modifier.height(12.dp))

                repeat(3) {
                    ShimmerBox(width = 300.dp, height = 14.dp, radius = 4.dp)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
