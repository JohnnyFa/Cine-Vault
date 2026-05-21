package com.fagundes.myshowlist.feat.catalog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.components.shimmer.ShimmerBox
import com.fagundes.myshowlist.feat.catalog.ui.components.shimmer.UpcomingMovieShimmer

@Composable
fun CatalogLoading(
    modifier: Modifier = Modifier,
    showSearchAndCategories: Boolean = true,
) {
    Column(modifier = modifier) {
        if (showSearchAndCategories) {
            // Search Bar Shimmer
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
            ) {
                ShimmerBox(
                    width = 400.dp,
                    height = 56.dp,
                    radius = 16.dp,
                )
            }

            Spacer(Modifier.height(16.dp))

            // Chips Shimmer
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                repeat(4) {
                    ShimmerBox(width = 80.dp, height = 32.dp, radius = 20.dp)
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // Featured Header Shimmer
        ShimmerBox(
            width = 120.dp,
            height = 20.dp,
            radius = 4.dp,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(12.dp))

        // Banner Shimmer
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(28.dp)),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ShimmerBox(
                    width = 500.dp,
                    height = 380.dp,
                    radius = 0.dp,
                )
            }

            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
            ) {
                ShimmerBox(width = 200.dp, height = 24.dp, radius = 4.dp)
                Spacer(Modifier.height(8.dp))
                repeat(2) {
                    ShimmerBox(width = 300.dp, height = 14.dp, radius = 4.dp)
                    Spacer(Modifier.height(6.dp))
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // List items
        repeat(3) {
            UpcomingMovieShimmer(modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(16.dp))
        }
    }
}
