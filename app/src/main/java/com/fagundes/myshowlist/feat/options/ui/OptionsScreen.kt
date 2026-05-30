package com.fagundes.myshowlist.feat.options.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fagundes.myshowlist.BuildConfig
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.feat.options.vm.ClearAction
import com.fagundes.myshowlist.feat.options.vm.OptionsUiState
import com.fagundes.myshowlist.feat.options.vm.OptionsViewModel
import com.fagundes.myshowlist.ui.theme.Background
import com.fagundes.myshowlist.ui.theme.Divider
import com.fagundes.myshowlist.ui.theme.MyShowListTheme
import com.fagundes.myshowlist.ui.theme.NeonRed
import com.fagundes.myshowlist.ui.theme.SurfaceElevated
import com.fagundes.myshowlist.ui.theme.TextMuted
import com.fagundes.myshowlist.ui.theme.TextPrimary
import com.fagundes.myshowlist.ui.theme.TextSecondary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OptionsScreen(
    onLogout: () -> Unit,
    viewModel: OptionsViewModel = koinViewModel(),
) {
    val user = viewModel.currentUser
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OptionsScreenContent(
        displayName = user?.displayName,
        email = user?.email,
        photoUrl = user?.photoUrl?.toString(),
        uiState = uiState,
        onLogout = { viewModel.logout(onLogout) },
        onRequestClear = viewModel::requestClear,
        onConfirmClear = viewModel::confirmClear,
        onDismissClear = viewModel::dismissClearDialog,
    )
}

@Composable
fun OptionsScreenContent(
    displayName: String?,
    email: String?,
    photoUrl: String?,
    uiState: OptionsUiState,
    onLogout: () -> Unit,
    onRequestClear: (ClearAction) -> Unit,
    onConfirmClear: () -> Unit,
    onDismissClear: () -> Unit,
) {
    ClearConfirmationDialog(
        action = uiState.pendingClearAction,
        onConfirm = onConfirmClear,
        onDismiss = onDismissClear,
    )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 72.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.label_options), style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(Modifier.height(24.dp))
        ProfileCard(displayName = displayName, email = email, photoUrl = photoUrl)
        Spacer(Modifier.height(32.dp))
        SectionLabel(stringResource(R.string.label_data_management))
        Spacer(Modifier.height(12.dp))
        DataManagementSection(uiState = uiState, onRequestClear = onRequestClear)
        Spacer(Modifier.height(32.dp))
        SectionLabel(stringResource(R.string.label_about))
        Spacer(Modifier.height(12.dp))
        AboutCard()
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
        ) {
            Text(stringResource(R.string.leave))
        }
    }
}

@Composable
private fun ClearConfirmationDialog(
    action: ClearAction?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (action == null) return

    val title =
        when (action) {
            ClearAction.Favorites -> stringResource(R.string.dialog_clear_favorites_title)
            ClearAction.Recents -> stringResource(R.string.dialog_clear_recents_title)
            ClearAction.Cache -> stringResource(R.string.dialog_clear_cache_title)
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(stringResource(R.string.dialog_clear_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.action_confirm), color = NeonRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        containerColor = SurfaceElevated,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
    )
}

@Composable
private fun DataManagementSection(
    uiState: OptionsUiState,
    onRequestClear: (ClearAction) -> Unit,
) {
    DataRow(
        icon = Icons.Filled.Favorite,
        label = stringResource(R.string.label_your_favorites),
        description = stringResource(R.string.desc_favorites_data),
        count = uiState.favoritesCount,
        onClear = { onRequestClear(ClearAction.Favorites) },
    )
    Spacer(Modifier.height(8.dp))
    DataRow(
        icon = Icons.Filled.History,
        label = stringResource(R.string.label_recently_viewed),
        description = stringResource(R.string.desc_recents_data),
        count = uiState.recentsCount,
        onClear = { onRequestClear(ClearAction.Recents) },
    )
    Spacer(Modifier.height(8.dp))
    DataRow(
        icon = Icons.Filled.Storage,
        label = stringResource(R.string.label_cache),
        description = stringResource(R.string.desc_cache_data),
        count = null,
        onClear = { onRequestClear(ClearAction.Cache) },
    )
}

@Composable
private fun AboutCard() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(SurfaceElevated, RoundedCornerShape(12.dp))
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            Text(
                text = "${stringResource(R.string.label_version)} ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun ProfileCard(
    displayName: String?,
    email: String?,
    photoUrl: String?,
) {
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
                modifier = Modifier.size(48.dp).clip(CircleShape),
            )
        } else {
            Box(
                modifier = Modifier.size(48.dp).background(NeonRed, CircleShape),
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
                Text(text = displayName, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            }
            if (!email.isNullOrBlank()) {
                Text(text = email, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelMedium, color = TextMuted)
}

@Composable
private fun DataRow(
    icon: ImageVector,
    label: String,
    description: String,
    count: Int?,
    onClear: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(SurfaceElevated, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    if (count != null) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier =
                                Modifier
                                    .background(Divider, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(text = count.toString(), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }

        TextButton(onClick = onClear) {
            Text(text = stringResource(R.string.action_clear), color = NeonRed, style = MaterialTheme.typography.labelMedium)
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
            uiState = OptionsUiState(favoritesCount = 12, recentsCount = 5),
            onLogout = {},
            onRequestClear = {},
            onConfirmClear = {},
            onDismissClear = {},
        )
    }
}
