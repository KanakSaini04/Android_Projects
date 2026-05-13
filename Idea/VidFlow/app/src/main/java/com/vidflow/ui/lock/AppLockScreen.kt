package com.vidflow.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.vidflow.navigation.Screen
import com.vidflow.security.AppLockManager
import com.vidflow.security.PinManager
import com.vidflow.ui.theme.Green400

@Composable
fun AppLockScreen(navController: NavController) {
    val context = LocalContext.current
    val lockManager = remember { AppLockManager(context) }
    val pinManager = remember { PinManager(context) }

    LaunchedEffect(Unit) {
        if (lockManager.isBiometricAvailable()) {
            lockManager.authenticate(
                activity = context as FragmentActivity,
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.AppLock.route) { inclusive = true }
                    }
                },
                onFallbackToPin = {
                    if (pinManager.hasPin()) {
                        navController.navigate(Screen.Pin.route("verify")) {
                            popUpTo(Screen.AppLock.route) { inclusive = true }
                        }
                    }
                }
            )
        } else if (pinManager.hasPin()) {
            navController.navigate(Screen.Pin.route("verify")) {
                popUpTo(Screen.AppLock.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.AppLock.route) { inclusive = true }
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("VidFlow", style = MaterialTheme.typography.headlineLarge, color = Green400)
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = Green400)
        }
    }
}