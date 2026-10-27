package com.fagundes.myshowlist.core.domain

sealed interface AuthSession {
    data class SignedIn(
        val user: AuthUser,
    ) : AuthSession

    data object Guest : AuthSession

    data object SignedOut : AuthSession
}
