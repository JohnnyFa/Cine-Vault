package com.fagundes.myshowlist.feat.login.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.components.ErrorText
import com.fagundes.myshowlist.feat.login.ui.components.GoogleLoginButton
import com.fagundes.myshowlist.feat.login.ui.components.LoginBackgroundDecorations
import com.fagundes.myshowlist.feat.login.ui.components.OrDivider
import com.fagundes.myshowlist.feat.login.ui.components.TermsAndPrivacyText
import com.fagundes.myshowlist.feat.login.vm.LoginUiEvent
import com.fagundes.myshowlist.feat.login.vm.LoginUiState
import com.fagundes.myshowlist.feat.login.vm.LoginViewModel
import com.fagundes.myshowlist.ui.theme.CineVaultGradients
import com.fagundes.myshowlist.ui.theme.TextMuted
import com.fagundes.myshowlist.ui.theme.TextPrimary
import com.fagundes.myshowlist.ui.theme.TextSecondary
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val webClientId = stringResource(R.string.default_web_client_id)

    val googleSignInClient =
        remember(webClientId) {
            val gso =
                GoogleSignInOptions.Builder(
                    GoogleSignInOptions.DEFAULT_SIGN_IN,
                )
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()

            GoogleSignIn.getClient(context, gso)
        }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let {
                    viewModel.onGoogleTokenReceived(it)
                }
            } catch (_: ApiException) {
            }
        }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is LoginUiEvent.NavigateHome) {
                onLoginSuccess()
            }
        }
    }

    LoginScreenContent(
        state = state,
        onLoginClick = {
            launcher.launch(googleSignInClient.signInIntent)
        },
    )
}

@Composable
fun LoginScreenContent(
    state: LoginUiState,
    onLoginClick: () -> Unit,
    onContinueAsGuest: () -> Unit = {},
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CineVaultGradients.SubtleBackground),
    ) {
        LoginBackgroundDecorations()

        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            Box(
                modifier =
                    Modifier
                        .size(120.dp)
                        .background(
                            brush = CineVaultGradients.Brand,
                            shape = RoundedCornerShape(28.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_logo_foreground),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(120.dp),
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Cine Vault",
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary,
                modifier = Modifier.testTag("login_app_name"),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.your_premium_cinema_experience),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(Modifier.height(40.dp))

            GoogleLoginButton(
                isLoading = state is LoginUiState.Loading,
                onClick = onLoginClick,
                modifier = Modifier.testTag("login_google_button"),
            )

            OrDivider()

            TextButton(
                onClick = onContinueAsGuest,
                modifier = Modifier.testTag("login_guest_button"),
            ) {
                Text(
                    text = stringResource(R.string.continue_as_guest),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
            }

            if (state is LoginUiState.Error) {
                Spacer(Modifier.height(16.dp))
                ErrorText(text = state.message)
            }

            Spacer(Modifier.weight(1f))

            TermsAndPrivacyText()
        }
    }
}

@Preview(name = "Loading")
@Composable
fun LoginLoadingPreview() {
    LoginScreenContent(
        state = LoginUiState.Loading,
        onLoginClick = {},
    )
}

@Preview(name = "Error")
@Composable
fun LoginErrorPreview() {
    LoginScreenContent(
        state = LoginUiState.Error(stringResource(R.string.authentication_failed)),
        onLoginClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        state = LoginUiState.Idle,
        onLoginClick = {},
    )
}
