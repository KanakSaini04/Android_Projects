package com.codexcraft.caretap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.codexcraft.caretap.navigation.CareTapNavGraph
import com.codexcraft.caretap.navigation.Screen
import com.codexcraft.caretap.ui.theme.CareTapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareTapTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            val items = listOf(
                                Triple(Screen.Home.route,     "Home",     "🏠"),
                                Triple(Screen.Apps.route,     "Apps",     "📱"),
                                Triple(Screen.Search.route,   "Search",   "🔍"),
                                Triple(Screen.Settings.route, "Settings", "⚙️"),
                            )
                            items.forEach { (route, label, icon) ->
                                NavigationBarItem(
                                    selected = currentRoute == route,
                                    onClick = {
                                        navController.navigate(route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Text(icon) },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    // ✅ FIX: innerPadding now applied so content is not hidden behind the bottom bar
                    CareTapNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}