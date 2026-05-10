package com.codexcraft.lensora.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codexcraft.lensora.core.theme.*
import com.codexcraft.lensora.core.util.Constants
import com.codexcraft.lensora.ui.camera.CameraScreen
import com.codexcraft.lensora.ui.edit.EditScreen
import com.codexcraft.lensora.ui.gallery.GalleryScreen
import com.codexcraft.lensora.ui.settings.SettingsScreen

data class TabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val lensoraTabs = listOf(
    TabItem("Camera", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
    TabItem("Vault", Icons.Filled.Lock, Icons.Outlined.Lock),
    TabItem("Edit", Icons.Filled.AutoFixHigh, Icons.Outlined.AutoFixHigh),
    TabItem("Engine", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun MainScreen(
    deepLinkPath: String? = null,
    viewModel: MainViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    // Handle deep links to legal sheets
    LaunchedEffect(deepLinkPath) {
        if (deepLinkPath != null && (
                    deepLinkPath.contains("privacy") || deepLinkPath.contains("terms")
                    )
        ) {
            viewModel.selectTab(Constants.TAB_SETTINGS)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack),
        containerColor = MatteBlack,
        contentColor = TextPrimary,
        bottomBar = {
            LensoraBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally(
                        initialOffsetX = { it * direction },
                        animationSpec = tween(280)
                    ) + fadeIn(tween(280))) togetherWith
                            (slideOutHorizontally(
                                targetOffsetX = { -it * direction },
                                animationSpec = tween(280)
                            ) + fadeOut(tween(280)))
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    Constants.TAB_CAMERA -> CameraScreen()
                    Constants.TAB_GALLERY -> GalleryScreen()
                    Constants.TAB_EDIT -> EditScreen()
                    Constants.TAB_SETTINGS -> SettingsScreen(deepLinkPath = deepLinkPath)
                    else -> CameraScreen()
                }
            }
        }
    }
}

@Composable
private fun LensoraBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(64.dp),
        containerColor = SurfaceDark,
        contentColor = TextSecondary,
        tonalElevation = 0.dp
    ) {
        lensoraTabs.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = LensoraTypography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = if (isSelected) ElectricBlue else TextMuted
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricBlue,
                    unselectedIconColor = TextMuted,
                    indicatorColor = ElectricBlueAlpha10
                )
            )
        }
    }
}