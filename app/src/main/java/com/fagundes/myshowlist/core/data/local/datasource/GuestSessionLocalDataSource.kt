package com.fagundes.myshowlist.core.data.local.datasource

interface GuestSessionLocalDataSource {
    fun isGuest(): Boolean

    fun setGuest(isGuest: Boolean)
}
