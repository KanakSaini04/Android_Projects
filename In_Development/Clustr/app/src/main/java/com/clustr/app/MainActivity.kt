package com.clustr.app

import android.Manifest
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clustr.app.auth.AuthViewModel
import com.clustr.app.ui.screens.AuthScreen
import com.clustr.app.ui.screens.library.LibraryScreen
import com.clustr.app.ui.screens.settings.SettingsScreen
import com.clustr.app.ui.screens.visualizer.VisualizerScreen
import com.clustr.app.ui.screens.visualizer.VisualizerViewModel
import com.clustr.app.ui.theme.*
import com.clustr.app.util.BiometricHelper

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            ClustrTheme {
                ClustrApp(activity = this)
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Visualizer : Screen("visualizer", "Visualizer", Icons.Rounded.GraphicEq)
    object Library    : Screen("library",    "Library",    Icons.Rounded.LibraryMusic)
    object Settings   : Screen("settings",   "Settings",   Icons.Rounded.Person)
}

val screens = listOf(Screen.Visualizer, Screen.Library, Screen.Settings)

@Composable
fun ClustrApp(activity: FragmentActivity) {
    val authVm: AuthViewModel = viewModel()
    val authState by authVm.state.collectAsState()

    // Permission
    var hasMicPermission by remember { mutableStateOf(false) }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasMicPermission = granted }

    LaunchedEffect(Unit) { permLauncher.launch(Manifest.permission.RECORD_AUDIO) }

    // Biometric gate
    var biometricPassed by remember { mutableStateOf(false) }
    LaunchedEffect(authState.isAuthenticated, authState.user) {
        val bio = authState.user?.biometricEnabled ?: false
        if (authState.isAuthenticated && bio && !biometricPassed) {
            if (BiometricHelper.canAuthenticate(activity)) {
                BiometricHelper.authenticate(
                    activity  = activity,
                    onSuccess = { biometricPassed = true },
                    onFailure = { /* keep locked */ }
                )
            } else {
                biometricPassed = true
            }
        } else {
            biometricPassed = true
        }
    }

    AnimatedContent(
        targetState = authState.isAuthenticated && biometricPassed,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { authenticated ->
        if (authenticated) {
            MainShell(
                authVm     = authVm,
                uid        = authState.user?.uid ?: "",
                micEnabled = authState.user?.micEnabled ?: true,
                hasMicPermission = hasMicPermission
            )
        } else if (!authState.isLoading && !authState.isAuthenticated) {
            AuthScreen(viewModel = authVm)
        } else {
            // Splash / loading
            Box(Modifier.fillMaxSize().background(Black), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent, modifier = Modifier.size(36.dp))
            }
        }
    }
}

@Composable
fun MainShell(
    authVm: AuthViewModel,
    uid: String,
    micEnabled: Boolean,
    hasMicPermission: Boolean
) {
    val visVm: VisualizerViewModel = viewModel()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Visualizer) }

    Box(Modifier.fillMaxSize().background(Black)) {
        // ── Screen content ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp) // nav bar height
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { screen ->
                when (screen) {
                    is Screen.Visualizer -> VisualizerScreen(
                        viewModel  = visVm,
                        micEnabled = micEnabled && hasMicPermission
                    )
                    is Screen.Library -> LibraryScreen(uid = uid)
                    is Screen.Settings -> SettingsScreen(
                        authViewModel = authVm,
                        onSignOut     = { /* handled by AuthViewModel auth state */ }
                    )
                }
            }
        }

        // ── Bottom navigation ──────────────────────────────────────────────────
        ClustrBottomNav(
            currentScreen = currentScreen,
            onSelect      = { currentScreen = it },
            modifier      = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ClustrBottomNav(
    currentScreen: Screen,
    onSelect: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Black)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = Divider, thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                NavItem(
                    screen    = screen,
                    selected  = currentScreen == screen,
                    onSelect  = { onSelect(screen) }
                )
            }
        }
    }
}

@Composable
fun NavItem(screen: Screen, selected: Boolean, onSelect: () -> Unit) {
    val tint = if (selected) Accent else TextSecondary
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            screen.icon,
            contentDescription = screen.label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        AnimatedVisibility(visible = selected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Accent)
            )
        }
    }
}