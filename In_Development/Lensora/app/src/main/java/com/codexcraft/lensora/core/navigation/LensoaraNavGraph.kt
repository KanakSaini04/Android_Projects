package com.codexcraft.lensora.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codexcraft.lensora.core.util.Constants
import com.codexcraft.lensora.ui.auth.AuthScreen
import com.codexcraft.lensora.ui.main.MainScreen
import com.codexcraft.lensora.ui.permissions.PermissionsScreen
import com.codexcraft.lensora.ui.splash.SplashScreen

@Composable
fun LensoraNavGraph(deepLinkPath: String? = null) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Constants.ROUTE_SPLASH,
        enterTransition = { fadeIn(tween(400)) },
        exitTransition = { fadeOut(tween(400)) }
    ) {
        composable(Constants.ROUTE_SPLASH) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Constants.ROUTE_AUTH) {
                        popUpTo(Constants.ROUTE_SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Constants.ROUTE_AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Constants.ROUTE_PERMISSIONS) {
                        popUpTo(Constants.ROUTE_AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Constants.ROUTE_PERMISSIONS) {
            PermissionsScreen(
                onPermissionsGranted = {
                    navController.navigate(Constants.ROUTE_MAIN) {
                        popUpTo(Constants.ROUTE_PERMISSIONS) { inclusive = true }
                    }
                }
            )
        }

        composable(Constants.ROUTE_MAIN) {
            MainScreen(deepLinkPath = deepLinkPath)
        }
    }
}