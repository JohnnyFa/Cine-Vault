package com.fagundes.myshowlist.feat.login.vm

sealed interface LoginUiEvent {
    object NavigateHome : LoginUiEvent
}
