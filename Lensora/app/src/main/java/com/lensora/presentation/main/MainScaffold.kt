package com.lensora.presentation.main

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lensora.core.ui.theme.*
import com.lensora.presentation.navigation.Screen

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Camera", Icons.Default.CameraAlt, Screen.Camera.route),
    BottomNavItem("Gallery", Icons.Default.PhotoLibrary, Screen.Gallery.route),
    BottomNavItem("Live", Icons.Default.QrCode2, Screen.LiveShare.route),
    BottomNavItem("Profile", Icons.Default.Person, Screen.Profile.route),
    BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings.route)
)

@Composable
fun MainScaffold(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Camera.route,
        Screen.Gallery.route,
        Screen.Profile.route,
        Screen.Settings.route,
        Screen.LiveShare.route
    )

    Scaffold(
        containerColor = Black,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                LensoraBottomNav(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Composable
fun LensoraBottomNav(
    navController: NavController,
    currentRoute: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Black.copy(alpha = 0.95f))
                )
            )
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceGray.copy(alpha = 0.9f))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Camera.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Box(contentAlignment = Alignment.Center) {
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(ElectricBlueDim.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                )
                            }
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selected) ElectricBlue else WhiteDim,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            color = if (selected) ElectricBlue else WhiteDim
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = ElectricBlue,
                        unselectedIconColor = WhiteDim
                    )
                )
            }
        }
    }
}
