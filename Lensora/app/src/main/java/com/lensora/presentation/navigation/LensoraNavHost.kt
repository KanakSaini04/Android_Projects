package com.lensora.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lensora.presentation.auth.AuthScreen
import com.lensora.presentation.camera.CameraScreen
import com.lensora.presentation.editor.EditorScreen
import com.lensora.presentation.gallery.GalleryScreen
import com.lensora.presentation.liveshare.LiveShareScreen
import com.lensora.presentation.main.MainScaffold
import com.lensora.presentation.onboarding.OnboardingScreen
import com.lensora.presentation.profile.ProfileScreen
import com.lensora.presentation.settings.SettingsScreen

@Composable
fun LensoraNavHost() {
    val navController = rememberNavController()

    MainScaffold(navController = navController) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Auth.route
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onFinish = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Auth.route) {
                AuthScreen(onAuthSuccess = {
                    navController.navigate(Screen.Camera.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Camera.route) {
                CameraScreen(navController = navController)
            }
            composable(Screen.Gallery.route) {
                GalleryScreen(navController = navController)
            }
            composable(Screen.Editor.route) { backStackEntry ->
                val photoUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                EditorScreen(navController = navController, photoUri = photoUri)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            composable(Screen.LiveShare.route) {
                LiveShareScreen(navController = navController)
            }
        }
    }
}
