package com.vidflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.vidflow.navigation.NavGraph
import com.vidflow.navigation.Screen
import com.vidflow.ui.components.BottomNav
import com.vidflow.ui.theme.AppTheme
import com.vidflow.ui.theme.VidFlowTheme
import com.vidflow.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = viewModel()
            val theme by settingsVm.theme.collectAsState()
            val appLock by settingsVm.appLock.collectAsState()

            val appTheme = when (theme) {
                "light"  -> AppTheme.LIGHT
                "dark"   -> AppTheme.DARK
                else     -> AppTheme.SYSTEM
            }

            VidFlowTheme(appTheme = appTheme) {
                val navController = rememberNavController()
                val start = if (appLock) Screen.AppLock.route else Screen.Home.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNav(navController) }
                ) { padding ->
                    NavGraph(
                        navController = navController,
                        startDestination = start,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}