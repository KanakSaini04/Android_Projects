package com.codexcraft.fileflow.ui.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.codexcraft.fileflow.ui.browse.BrowseScreen
import com.codexcraft.fileflow.ui.home.HomeScreen
import com.codexcraft.fileflow.ui.profile.ProfileScreen
import com.codexcraft.fileflow.ui.tools.ToolsScreen
import com.codexcraft.fileflow.ui.vault.VaultScreen

sealed class MainScreenTab(val route: String, val label: String, val icon: ImageVector) {
    data object Home : MainScreenTab("home", "Home", Icons.Default.Home)
    data object Browse : MainScreenTab("browse", "Browse", Icons.Default.Folder)
    data object Vault : MainScreenTab("vault", "Vault", Icons.Default.Security)
    data object Tools : MainScreenTab("tools", "Tools", Icons.Default.Build)
    data object Profile : MainScreenTab("profile", "Profile", Icons.Default.Person)
}

private val tabs = listOf(
    MainScreenTab.Home,
    MainScreenTab.Browse,
    MainScreenTab.Vault,
    MainScreenTab.Tools,
    MainScreenTab.Profile
)

@Composable
fun MainScreen(rootNavController: NavController) {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MainScreenTab.Home.route

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)) {
                tabs.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            tabNavController.navigate(screen.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = MainScreenTab.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it / 10 }) },
            exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it / 10 }) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it / 10 }) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it / 10 }) }
        ) {
            composable(MainScreenTab.Home.route) { HomeScreen(navController = rootNavController) }
            composable(MainScreenTab.Browse.route) { BrowseScreen(navController = rootNavController) }
            composable(MainScreenTab.Vault.route) { VaultScreen() }
            composable(MainScreenTab.Tools.route) { ToolsScreen(navController = rootNavController) }
            composable(MainScreenTab.Profile.route) { ProfileScreen(rootNavController = rootNavController) }
        }
    }
}
