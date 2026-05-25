package com.fagundes.myshowlist.feat.home.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.home.vm.HomeUiState
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val movie = Movie(1, "Test Movie", "url", "Overview", 8.0)
    private val movieList = listOf(movie)

    @Test
    fun homeScreen_loadingState_displaysSkeletons() {
        composeTestRule.setContent {
            HomeScreenContent(
                trendingState = HomeUiState.Loading,
                forYouState = HomeUiState.Loading,
                showOfTheDayState = HomeUiState.Loading,
                favoritesState = HomeUiState.Loading,
                recentsState = HomeUiState.Loading,
                onOpenDetail = { _, _ -> },
                onRetry = HomeRetryActions({}, {}, {}),
            )
        }

        composeTestRule.onNodeWithTag("show_of_the_day_container").assertIsDisplayed()
        composeTestRule.onNodeWithTag("trending_now_container").assertIsDisplayed()
        composeTestRule.onNodeWithTag("for_you_container").assertIsDisplayed()
    }

    @Test
    fun homeScreen_successState_displaysContent() {
        composeTestRule.setContent {
            HomeScreenContent(
                trendingState = HomeUiState.Success(movieList),
                forYouState = HomeUiState.Success(movieList),
                showOfTheDayState = HomeUiState.Success(movie),
                favoritesState = HomeUiState.Success(movieList),
                recentsState = HomeUiState.Success(movieList),
                onOpenDetail = { _, _ -> },
                onRetry = HomeRetryActions({}, {}, {}),
            )
        }

        // We use onAllNodesWithText(movie.title).onFirst() because the same movie might appear in multiple sections
        composeTestRule.onAllNodesWithText("Test Movie", substring = true).onFirst()
            .assertIsDisplayed()

        // Scroll to the container instead of searching by text directly while scrolling might be more reliable if items are recycled
        composeTestRule.onNodeWithTag("home_screen_content")
            .performScrollToNode(hasTestTag("trending_now_container"))
        composeTestRule.onNodeWithTag("trending_now_container").assertIsDisplayed()

        composeTestRule.onNodeWithTag("home_screen_content")
            .performScrollToNode(hasTestTag("for_you_container"))
        composeTestRule.onNodeWithTag("for_you_container").assertIsDisplayed()
    }

    @Test
    fun homeScreen_errorState_displaysErrorSectionAndRetryWorks() {
        var retryClicked = false
        composeTestRule.setContent {
            HomeScreenContent(
                trendingState = HomeUiState.Error("Error"),
                forYouState = HomeUiState.Error("Error"),
                showOfTheDayState = HomeUiState.Error("Error"),
                favoritesState = HomeUiState.Idle,
                recentsState = HomeUiState.Idle,
                onOpenDetail = { _, _ -> },
                onRetry =
                    HomeRetryActions(
                        onRetryShowOfTheDay = { retryClicked = true },
                        onRetryPopular = {},
                        onRetryRecommended = {},
                    ),
            )
        }

        // Multiple ErrorSections will be present, check if at least one is displayed
        composeTestRule.onAllNodesWithTag("error_section").onFirst().assertIsDisplayed()

        // Click retry on the first retry button found
        composeTestRule.onAllNodesWithTag("error_retry_button").onFirst().performClick()

        assert(retryClicked)
    }
}
