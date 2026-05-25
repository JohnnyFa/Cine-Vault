package com.fagundes.myshowlist.feat.options.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.feat.options.vm.OptionsViewModel
import com.fagundes.myshowlist.ui.theme.Background
import com.fagundes.myshowlist.ui.theme.MyShowListTheme
import com.fagundes.myshowlist.ui.theme.NeonRed
import com.fagundes.myshowlist.ui.theme.SurfaceElevated
import com.fagundes.myshowlist.ui.theme.TextPrimary
import com.fagundes.myshowlist.ui.theme.TextSecondary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OptionsScreen(
    onLogout: () -> Unit,
    viewModel: OptionsViewModel = koinViewModel(),
) {
    val user = viewModel.currentUser

    OptionsScreenContent(
        displayName = user?.displayName,
        email = user?.email,
        photoUrl = user?.photoUrl?.toString(),
        onLogout = { viewModel.logout(onLogout) },
    )
}

@Composable
fun OptionsScreenContent(
    displayName: String?,
    email: String?,
    photoUrl: String?,
    onLogout: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .safeDrawingPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 72.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.label_options),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(SurfaceElevated, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .background(NeonRed, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = displayName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column {
                if (!displayName.isNullOrBlank()) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                    )
                }
                if (!email.isNullOrBlank()) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = NeonRed,
                ),
        ) {
            Text(stringResource(R.string.leave))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OptionsScreenPreview() {
    MyShowListTheme {
        OptionsScreenContent(
            displayName = "John Doe",
            email = "john.doe@example.com",
            photoUrl = null,
            onLogout = {},
        )
    }
}
