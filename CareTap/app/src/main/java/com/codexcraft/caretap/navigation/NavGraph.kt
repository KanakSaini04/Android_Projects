package com.codexcraft.caretap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.codexcraft.caretap.ui.screens.AppsScreen
import com.codexcraft.caretap.ui.screens.HomeScreen
import com.codexcraft.caretap.ui.screens.SearchScreen
import com.codexcraft.caretap.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Apps     : Screen("apps")
    object Search   : Screen("search")
    object Settings : Screen("settings")
}

@Composable
fun CareTapNavGraph(
    navController: NavHostController,
    // ✅ FIX: modifier accepted here so Scaffold's innerPadding flows into content
    modifier: Modifier = Modifier
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Home.route,
        modifier         = modifier          // ← applied to the NavHost container
    ) {
        composable(Screen.Home.route)     { HomeScreen()     }
        composable(Screen.Apps.route)     { AppsScreen()     }
        composable(Screen.Search.route)   { SearchScreen()   }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}
