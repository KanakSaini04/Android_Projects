package com.codexcraft.fileflow.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codexcraft.fileflow.ui.main.MainScreen
import com.codexcraft.fileflow.ui.profile.LegalScreen
import com.codexcraft.fileflow.ui.reader.ReaderScreen
import com.codexcraft.fileflow.ui.splash.SplashScreen
import com.codexcraft.fileflow.ui.tools.CleanerScreen
import com.codexcraft.fileflow.ui.tools.FlowShareScreen
import com.codexcraft.fileflow.ui.tools.ImageToPdfScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onAnimationFinished = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN) {
            MainScreen(rootNavController = navController)
        }
        composable(
            route = Routes.LEGAL,
            arguments = listOf(
                navArgument("docType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val docType = backStackEntry.arguments?.getString("docType")
            LegalScreen(docType = docType, onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.READER,
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("mime") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri").orEmpty()
            val mime = backStackEntry.arguments?.getString("mime") ?: "*/*"
            ReaderScreen(uriString = uri, mimeString = mime)
        }
        composable(Routes.IMAGE_TO_PDF) { ImageToPdfScreen() }
        composable(Routes.FLOW_SHARE) { FlowShareScreen() }
        composable(Routes.CLEANER) { CleanerScreen() }
    }
}
