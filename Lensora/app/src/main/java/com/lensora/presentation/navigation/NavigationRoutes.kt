package com.lensora.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Camera : Screen("camera")
    object Editor : Screen("editor/{photoUri}") {
        fun createRoute(photoUri: String) = "editor/$photoUri"
    }
    object Gallery : Screen("gallery")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object LiveShare : Screen("live_share")
}
