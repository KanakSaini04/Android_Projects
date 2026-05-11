package com.example.qrforge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.qrforge.ui.screens.*
import com.example.qrforge.ui.theme.QRForgeTheme
import com.example.qrforge.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Scan     : Screen("scan",     "Scan QR",   Icons.Filled.QrCodeScanner,  Icons.Outlined.QrCodeScanner)
    object Create   : Screen("create",   "Create QR", Icons.Filled.QrCode,         Icons.Outlined.QrCode)
    object History  : Screen("history",  "History",   Icons.Filled.History,        Icons.Outlined.History)
    object Settings : Screen("settings", "Settings",  Icons.Filled.Settings,       Icons.Outlined.Settings)
}

val bottomNavItems = listOf(Screen.Scan, Screen.Create, Screen.History, Screen.Settings)

@AndroidEntryPoint
class MainActivity : androidx.fragment.app.FragmentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            var isUnlocked by remember { mutableStateOf(false) }
            var isAuthenticated by remember { mutableStateOf(false) }

            QRForgeTheme(darkTheme = settings.isDarkTheme) {
                if (!settings.onboardingDone) {
                    OnboardingScreen(onComplete = {
                        settingsViewModel.setOnboardingDone(true)
                    })
                } else if (!isAuthenticated) {
                    AuthScreen(onAuthComplete = { isAuthenticated = true })
                } else if (settings.biometricLock && !isUnlocked) {
                    BiometricLockScreen(
                        onUseBiometric = {
                            showBiometricPrompt { isUnlocked = true }
                        }
                    )
                } else {
                    QRForgeNavHost()
                }
            }
        }
    }

    private fun showBiometricPrompt(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) { onSuccess() }
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("QRForge")
            .setSubtitle("Authenticate to continue")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        BiometricPrompt(
            this as androidx.fragment.app.FragmentActivity,
            executor,
            callback
        ).authenticate(promptInfo)
    }
}

@Composable
fun BiometricLockScreen(onUseBiometric: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.surfaceVariant
                )
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                Modifier.size(96.dp).clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.QrCode, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row {
                    Text(
                        "QR",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "Forge",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    "Craft. Scan. Connect.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Icon(
                Icons.Filled.Fingerprint, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )

            Text(
                "Authentication Required",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onUseBiometric,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Unlock QRForge",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun QRForgeNavHost() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { QRForgeBottomNav(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Scan.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() + slideInHorizontally() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() + slideOutHorizontally() }
        ) {
            composable(Screen.Scan.route)     { ScanScreen(hiltViewModel()) }
            composable(Screen.Create.route)   { CreateScreen(hiltViewModel()) }
            composable(Screen.History.route)  { HistoryScreen(hiltViewModel()) }
            composable(Screen.Settings.route) { SettingsScreen(hiltViewModel()) }
        }
    }
}

@Composable
fun QRForgeBottomNav(navController: androidx.navigation.NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy
                ?.any { it.route == screen.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (selected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    indicatorColor      = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}