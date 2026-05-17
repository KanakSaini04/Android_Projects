package com.lensora.presentation.splash

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lensora.presentation.navigation.Screen

@Composable
fun SplashRouter(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.startDestination.collectAsState()

    LaunchedEffect(destination) {
        destination?.let { dest ->
            navController.navigate(dest) {
                // Clear the splash screen from the backstack
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }
}
