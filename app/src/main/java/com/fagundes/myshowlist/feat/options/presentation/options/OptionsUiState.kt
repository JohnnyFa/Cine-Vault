package com.fagundes.myshowlist.feat.options.presentation.options

import com.fagundes.myshowlist.core.domain.AuthUser

data class OptionsUiState(
    val user: AuthUser? = null,
    val favoritesCount: Int = 0,
    val recentsCount: Int = 0,
    val pendingClearAction: ClearAction? = null,
)

sealed interface ClearAction {
    data object Favorites : ClearAction

    data object Recents : ClearAction

    data object Cache : ClearAction
}

sealed interface OptionsEvent {
    data object LoggedOut : OptionsEvent
}
