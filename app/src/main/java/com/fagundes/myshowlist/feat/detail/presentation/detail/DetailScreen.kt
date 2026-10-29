package com.fagundes.myshowlist.feat.detail.presentation.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.components.LoadingSection
import com.fagundes.myshowlist.components.SignInRequiredDialog
import com.fagundes.myshowlist.components.error.ErrorSection
import com.fagundes.myshowlist.feat.detail.domain.model.ContentDetail
import com.fagundes.myshowlist.feat.detail.presentation.components.FavoriteButton
import com.fagundes.myshowlist.feat.detail.presentation.components.MetaRow
import com.fagundes.myshowlist.feat.detail.presentation.components.PosterHero
import com.fagundes.myshowlist.ui.theme.Background
import com.fagundes.myshowlist.ui.theme.BlackSoft
import com.fagundes.myshowlist.ui.theme.TextPrimary
import com.fagundes.myshowlist.ui.theme.TextSecondary

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onRequestSignIn: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSignInDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is DetailEvent.FavoriteUpdated -> Unit
                is DetailEvent.RequireSignIn -> showSignInDialog = true
            }
        }
    }

    if (showSignInDialog) {
        SignInRequiredDialog(
            onSignIn = {
                showSignInDialog = false
                onRequestSignIn()
            },
            onDismiss = { showSignInDialog = false },
        )
    }

    when (state) {
        is DetailUiState.Loading -> LoadingSection()
        is DetailUiState.Error ->
            DetailErrorContent(
                message = stringResource((state as DetailUiState.Error).message),
                onRetry = viewModel::retry,
                onBack = onBack,
            )

        is DetailUiState.Success -> {
            val successState = state as DetailUiState.Success
            DetailContent(
                ui = successState.ui,
                isFavorite = successState.isFavorite,
                isFavoriteLoading = successState.isFavoriteLoading,
                onFavoriteClick = viewModel::onFavoriteClick,
                onBack = onBack,
            )
        }
    }
}

// Without the poster hero there's no back button, so the error screen brings its own.
@Composable
private fun DetailErrorContent(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .safeDrawingPadding(),
    ) {
        IconButton(
            onClick = onBack,
            modifier =
                Modifier
                    .padding(16.dp)
                    .background(color = BlackSoft.copy(alpha = 0.6f), shape = CircleShape)
                    .testTag("detail_error_back"),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
                tint = Color.White,
            )
        }

        ErrorSection(
            onRetry = onRetry,
            message = message,
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 24.dp),
        )
    }
}

@Composable
fun DetailContent(
    ui: ContentDetail,
    isFavorite: Boolean,
    isFavoriteLoading: Boolean,
    onFavoriteClick: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState()),
    ) {
        PosterHero(
            imageUrl = ui.imageUrl,
            onBack = onBack,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = ui.title,
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
            )

            Spacer(Modifier.height(12.dp))

            MetaRow(
                rating = ui.rating,
                type = ui.type,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = ui.overview ?: stringResource(R.string.empty_overview),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp,
            )

            Spacer(Modifier.height(32.dp))

            FavoriteButton(
                isFavorite = isFavorite,
                isLoading = isFavoriteLoading,
                onClick = onFavoriteClick,
            )

            Spacer(Modifier.height(48.dp))
        }
    }
}
