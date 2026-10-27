package com.fagundes.myshowlist.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.ui.theme.NeonRed
import com.fagundes.myshowlist.ui.theme.SurfaceElevated
import com.fagundes.myshowlist.ui.theme.TextPrimary
import com.fagundes.myshowlist.ui.theme.TextSecondary

/**
 * Shown when a guest reaches a feature that needs an account.
 */
@Composable
fun SignInRequiredDialog(
    onSignIn: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_sign_in_required_title)) },
        text = { Text(stringResource(R.string.dialog_sign_in_required_message)) },
        confirmButton = {
            TextButton(onClick = onSignIn) {
                Text(stringResource(R.string.sign_in), color = NeonRed)
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
