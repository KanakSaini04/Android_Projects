package com.codexcraft.fileflow.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codexcraft.fileflow.presentation.browse.BrowseScreen
import com.codexcraft.fileflow.presentation.home.HomeScreen
import com.codexcraft.fileflow.presentation.profile.ProfileScreen
import com.codexcraft.fileflow.presentation.profile.legal.PrivacyPolicyScreen
import com.codexcraft.fileflow.presentation.profile.legal.TermsScreen
import com.codexcraft.fileflow.presentation.reader.UniversalReaderScreen
import com.codexcraft.fileflow.presentation.splash.SplashScreen
import com.codexcraft.fileflow.presentation.tools.ToolsScreen
import com.codexcraft.fileflow.presentation.tools.features.CleanerScreen
import com.codexcraft.fileflow.presentation.tools.features.FlowShareScreen
import com.codexcraft.fileflow.presentation.tools.features.ImageToPdfScreen
import com.codexcraft.fileflow.presentation.vault.VaultScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Browse.route,
        Screen.Vault.route,
        Screen.Tools.route,
        Screen.Profile.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToReader = { fileUri ->
                        navController.navigate(Screen.Reader.createRoute(fileUri))
                    }
                )
            }
            
            composable(Screen.Browse.route) {
                BrowseScreen(
                    onNavigateToReader = { fileUri ->
                        navController.navigate(Screen.Reader.createRoute(fileUri))
                    }
                )
            }
            
            composable(Screen.Vault.route) {
                VaultScreen()
            }
            
            composable(Screen.Tools.route) {
                ToolsScreen(
                    onNavigateToTool = { route ->
                        navController.navigate(route)
                    }
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToPrivacyPolicy = {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    },
                    onNavigateToTerms = {
                        navController.navigate(Screen.Terms.route)
                    }
                )
            }
            
            composable(
                route = Screen.Reader.route,
                arguments = listOf(navArgument("fileUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val fileUri = backStackEntry.arguments?.getString("fileUri") ?: ""
                UniversalReaderScreen(
                    fileUri = fileUri,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Terms.route) {
                TermsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Tools Feature Routes
            composable(Screen.ImageToPdf.route) {
                ImageToPdfScreen(onBack = { navController.popBackStack() })
            }
            
            composable(Screen.FlowShare.route) {
                FlowShareScreen(onBack = { navController.popBackStack() })
            }
            
            composable(Screen.Cleaner.route) {
                CleanerScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
