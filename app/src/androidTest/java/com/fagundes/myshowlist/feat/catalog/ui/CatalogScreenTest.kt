package com.fagundes.myshowlist.feat.catalog.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.fagundes.myshowlist.core.domain.Movie
import com.fagundes.myshowlist.feat.catalog.vm.CatalogContentState
import com.fagundes.myshowlist.feat.catalog.vm.CatalogUiState
import com.fagundes.myshowlist.ui.theme.MyShowListTheme
import org.junit.Rule
import org.junit.Test

class CatalogScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun catalogScreen_loadingState_displaysLoading() {
        composeTestRule.setContent {
            MyShowListTheme {
                CatalogScreenContent(
                    state = CatalogUiState.Loading,
                    searchQuery = "",
                    onSearchChange = {},
                    onCategorySelected = {},
                    onRetry = {},
                    onSeeAllUpcoming = {},
                    onOpenDetail = { _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithTag("CatalogLoading").assertIsDisplayed()
    }

    @Test
    fun catalogScreen_errorState_displaysError() {
        composeTestRule.setContent {
            MyShowListTheme {
                CatalogScreenContent(
                    state = CatalogUiState.Error("Error"),
                    searchQuery = "",
                    onSearchChange = {},
                    onCategorySelected = {},
                    onRetry = {},
                    onSeeAllUpcoming = {},
                    onOpenDetail = { _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithTag("CatalogError").assertIsDisplayed()
        composeTestRule.onNodeWithTag("error_retry_button").assertIsDisplayed()
    }

    @Test
    fun catalogScreen_emptyState_displaysEmpty() {
        composeTestRule.setContent {
            MyShowListTheme {
                CatalogScreenContent(
                    state = CatalogUiState.Content(CatalogContentState(movies = emptyList())),
                    searchQuery = "Search",
                    onSearchChange = {},
                    onCategorySelected = {},
                    onRetry = {},
                    onSeeAllUpcoming = {},
                    onOpenDetail = { _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithTag("CatalogEmpty").assertIsDisplayed()
        composeTestRule.onNodeWithText("No movies found").assertIsDisplayed()
    }

    @Test
    fun catalogScreen_contentState_displaysMovies() {
        val movies =
            listOf(
                Movie(1, "Movie 1", "/url1", "Overview 1", 8.0),
                Movie(2, "Movie 2", "/url2", "Overview 2", 7.0),
            )
        composeTestRule.setContent {
            MyShowListTheme {
                CatalogScreenContent(
                    state = CatalogUiState.Content(CatalogContentState(movies = movies, featuredMovie = movies[0])),
                    searchQuery = "",
                    onSearchChange = {},
                    onCategorySelected = {},
                    onRetry = {},
                    onSeeAllUpcoming = {},
                    onOpenDetail = { _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithTag("CatalogMovieList").assertIsDisplayed()
        composeTestRule.onNodeWithText("Movie 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Movie 2").assertIsDisplayed()
    }
}
