package com.fagundes.myshowlist.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fagundes.myshowlist.components.bottomnavigation.MainScaffold
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.feat.catalog.ui.CatalogScreen
import com.fagundes.myshowlist.feat.catalog.ui.UpcomingScreen
import com.fagundes.myshowlist.feat.catalog.vm.CatalogViewModel
import com.fagundes.myshowlist.feat.catalog.vm.UpcomingViewModel
import com.fagundes.myshowlist.feat.detail.ui.DetailScreen
import com.fagundes.myshowlist.feat.home.ui.HomeScreen
import com.fagundes.myshowlist.feat.home.vm.HomeViewModel
import com.fagundes.myshowlist.feat.login.ui.LoginScreen
import com.fagundes.myshowlist.feat.options.ui.OptionsScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(startDestination: String) {
    val navController = rememberNavController()

    MainScaffold(navController = navController) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable(AppRoutes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    },
                )
            }

            composable(AppRoutes.HOME) {
                val viewModel: HomeViewModel = koinViewModel()

                HomeScreen(
                    viewModel = viewModel,
                    onOpenDetail = { id, type ->
                        navController.navigate(AppRoutes.detail(id, type))
                    },
                )
            }

            composable(AppRoutes.CATALOG) {
                val viewModel: CatalogViewModel = koinViewModel()
                CatalogScreen(
                    viewModel = viewModel,
                    onOpenDetail = { id, type ->
                        navController.navigate(AppRoutes.detail(id, type))
                    },
                    onSeeAllUpcoming = {
                        navController.navigate(AppRoutes.UPCOMING)
                    },
                )
            }

            composable(AppRoutes.UPCOMING) {
                val viewModel: UpcomingViewModel = koinViewModel()
                UpcomingScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenDetail = { id, type ->
                        navController.navigate(AppRoutes.detail(id, type))
                    },
                )
            }

            composable(AppRoutes.OPTIONS) {
                OptionsScreen(
                    onLogout = {
                        navController.navigate(AppRoutes.LOGIN) {
                            popUpTo(0)
                        }
                    },
                )
            }

            composable(AppRoutes.DETAIL) { backStackEntry ->
                val id = backStackEntry.arguments!!.getString("id")!!.toInt()

                val type =
                    ContentType.valueOf(
                        backStackEntry.arguments!!.getString("type")!!,
                    )

                DetailScreen(
                    id = id,
                    type = type,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
