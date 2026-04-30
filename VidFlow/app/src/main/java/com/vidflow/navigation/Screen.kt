package com.vidflow.navigation

sealed class Screen(val route: String) {
    object Home      : Screen("home")
    object Preview   : Screen("preview")
    object Downloads : Screen("downloads")
    object History   : Screen("history")
    object Settings  : Screen("settings")
    object AppLock   : Screen("app_lock")
    object Pin       : Screen("pin/{mode}") {
        fun route(mode: String) = "pin/$mode"
    }
}