package com.fagundes.myshowlist.feat.detail.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fagundes.myshowlist.R
import com.fagundes.myshowlist.components.LoadingSection
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.detail.domain.ContentDetailUi
import com.fagundes.myshowlist.feat.detail.ui.components.FavoriteButton
import com.fagundes.myshowlist.feat.detail.ui.components.MetaRow
import com.fagundes.myshowlist.feat.detail.ui.components.PosterHero
import com.fagundes.myshowlist.feat.detail.vm.DetailEvent
import com.fagundes.myshowlist.feat.detail.vm.DetailUiState
import com.fagundes.myshowlist.feat.detail.vm.DetailViewModel
import com.fagundes.myshowlist.ui.theme.Background
import com.fagundes.myshowlist.ui.theme.TextPrimary
import com.fagundes.myshowlist.ui.theme.TextSecondary
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailScreen(
    id: Int,
    type: ContentType,
    onBack: () -> Unit,
    viewModel: DetailViewModel =
        koinViewModel {
            parametersOf(id, type)
        },
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is DetailEvent.FavoriteUpdated -> Unit
            }
        }
    }

    when (state) {
        is DetailUiState.Loading -> LoadingSection()
        is DetailUiState.Error -> {
            Text(
                text = (state as DetailUiState.Error).message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }

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

@Composable
fun DetailContent(
    ui: ContentDetailUi,
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
