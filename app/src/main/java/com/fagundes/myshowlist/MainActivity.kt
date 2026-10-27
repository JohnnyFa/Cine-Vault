package com.fagundes.myshowlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fagundes.myshowlist.core.domain.AuthSession
import com.fagundes.myshowlist.core.domain.repository.AuthRepository
import com.fagundes.myshowlist.core.navigation.AppNavGraph
import com.fagundes.myshowlist.ui.theme.MyShowListTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val authRepository: AuthRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Guests skip the login screen too: their session persists across launches.
        val hasSession = authRepository.currentSession() != AuthSession.SignedOut

        setContent {
            MyShowListTheme {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                ) {
                    AppNavGraph(
                        startDestination =
                            if (hasSession) {
                                AppRoutes.HOME
                            } else {
                                AppRoutes.LOGIN
                            },
                    )
                }
            }
        }
    }
}
