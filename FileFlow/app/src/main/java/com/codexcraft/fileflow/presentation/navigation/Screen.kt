package com.codexcraft.fileflow.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Browse : Screen("browse")
    object Vault : Screen("vault")
    object Tools : Screen("tools")
    object Profile : Screen("profile")
    object Reader : Screen("reader/{fileUri}") {
        fun createRoute(fileUri: String) = "reader/$fileUri"
    }
    object PrivacyPolicy : Screen("privacy_policy")
    object Terms : Screen("terms")
    
    // Tools Features
    object ImageToPdf : Screen("image_to_pdf")
    object FlowShare : Screen("flow_share")
    object Cleaner : Screen("cleaner")
}
