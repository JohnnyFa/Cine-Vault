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
import com.fagundes.myshowlist.core.navigation.AppNavGraph
import com.fagundes.myshowlist.core.navigation.AppRoutes
import com.fagundes.myshowlist.ui.theme.MyShowListTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val isUserLoggedIn =
            FirebaseAuth.getInstance().currentUser != null

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
                            if (isUserLoggedIn) {
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
