package com.codexcraft.caretap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.codexcraft.caretap.ui.screens.AppsScreen
import com.codexcraft.caretap.ui.screens.HomeScreen
import com.codexcraft.caretap.ui.screens.SearchScreen
import com.codexcraft.caretap.ui.screens.SettingsScreen
import com.codexcraft.caretap.ui.viewmodel.ProfileViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Apps : Screen("apps")
    object Search : Screen("search")
    object Settings : Screen("settings")
}

@Composable
fun CareTapNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = profileViewModel)
        }
        composable(Screen.Apps.route) {
            AppsScreen(viewModel = profileViewModel)
        }
        composable(Screen.Search.route) {
            SearchScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}