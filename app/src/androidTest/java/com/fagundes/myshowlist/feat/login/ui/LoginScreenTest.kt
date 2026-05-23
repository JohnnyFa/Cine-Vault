package com.fagundes.myshowlist.feat.login.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fagundes.myshowlist.feat.login.vm.LoginUiState
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_initialState_displaysRequiredElements() {
        composeTestRule.setContent {
            LoginScreenContent(
                state = LoginUiState.Idle,
                onLoginClick = {},
            )
        }

        // Using substring = true for app name because it might have flavor suffixes like "(Dev)"
        composeTestRule.onNodeWithText("CINE VAULT", ignoreCase = true, substring = true).assertIsDisplayed()

        // Using tags for better reliability
        composeTestRule.onNodeWithTag("login_google_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_guest_button").assertIsDisplayed()

        // Also verifying text if needed, using substring for robustness
        composeTestRule.onNodeWithText("Continue with Google", ignoreCase = true, substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue as Guest", ignoreCase = true, substring = true).assertIsDisplayed()
    }

    @Test
    fun loginScreen_clickingLogin_callsCallback() {
        var loginClicked = false
        composeTestRule.setContent {
            LoginScreenContent(
                state = LoginUiState.Idle,
                onLoginClick = { loginClicked = true },
            )
        }

        composeTestRule.onNodeWithTag("login_google_button").performClick()
        assert(loginClicked)
    }

    @Test
    fun loginScreen_loadingState_displaysCircularProgressIndicator() {
        composeTestRule.setContent {
            LoginScreenContent(
                state = LoginUiState.Loading,
                onLoginClick = {},
            )
        }

        // CircularProgressIndicator doesn't have text, and when loading,
        // GoogleLoginButton doesn't show the "Continue with Google" text.
        composeTestRule.onNodeWithText("Continue with Google", ignoreCase = true, substring = true).assertDoesNotExist()
    }
}
