package com.vidflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vidflow.ui.lock.AppLockScreen
import com.vidflow.ui.lock.PinScreen
import com.vidflow.ui.screens.DownloadManagerScreen
import com.vidflow.ui.screens.HistoryScreen
import com.vidflow.ui.screens.HomeScreen
import com.vidflow.ui.screens.PreviewScreen
import com.vidflow.ui.screens.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Preview.route) {
            PreviewScreen(navController)
        }
        composable(Screen.Downloads.route) {
            DownloadManagerScreen(navController)
        }
        composable(Screen.History.route) {
            HistoryScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.AppLock.route) {
            AppLockScreen(navController)
        }
        composable(
            route = Screen.Pin.route,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStack ->
            val mode = backStack.arguments?.getString("mode") ?: "verify"
            PinScreen(navController, mode)
        }
    }
}