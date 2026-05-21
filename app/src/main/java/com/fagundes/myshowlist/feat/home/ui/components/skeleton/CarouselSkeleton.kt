package com.fagundes.myshowlist.feat.home.ui.components.skeleton

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
fun CarouselSkeleton() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Title Shimmer
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBox(width = 20.dp, height = 20.dp, radius = 4.dp)
            Spacer(modifier = Modifier.width(8.dp))
            ShimmerBox(width = 120.dp, height = 24.dp, radius = 4.dp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.padding(start = 16.dp),
        ) {
            repeat(4) {
                Column(modifier = Modifier.width(140.dp)) {
                    ShimmerBox(
                        width = 140.dp,
                        height = 200.dp,
                        radius = 24.dp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ShimmerBox(width = 100.dp, height = 16.dp, radius = 4.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(width = 40.dp, height = 14.dp, radius = 4.dp)
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}
