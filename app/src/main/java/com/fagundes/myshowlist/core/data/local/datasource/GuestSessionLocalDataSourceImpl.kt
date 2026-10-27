package com.fagundes.myshowlist.core.data.local.datasource

import android.content.SharedPreferences
import androidx.core.content.edit

class GuestSessionLocalDataSourceImpl(
    private val prefs: SharedPreferences,
) : GuestSessionLocalDataSource {
    override fun isGuest(): Boolean = prefs.getBoolean(KEY_IS_GUEST, false)

    override fun setGuest(isGuest: Boolean) {
        prefs.edit { putBoolean(KEY_IS_GUEST, isGuest) }
    }

    companion object {
        const val PREFS_NAME = "auth_prefs"
        private const val KEY_IS_GUEST = "is_guest"
    }
}
