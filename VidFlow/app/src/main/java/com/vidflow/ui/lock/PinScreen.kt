package com.vidflow.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vidflow.navigation.Screen
import com.vidflow.security.PinManager
import com.vidflow.ui.theme.Green400

@Composable
fun PinScreen(navController: NavController, mode: String) {
    val context = LocalContext.current
    val pinManager = remember { PinManager(context) }

    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var step by remember { mutableStateOf(1) }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("VidFlow", style = MaterialTheme.typography.headlineLarge, color = Green400)
            Spacer(Modifier.height(32.dp))

            Text(
                text = when {
                    mode == "verify"     -> "Enter your PIN"
                    step == 1            -> "Create a PIN"
                    else                 -> "Confirm your PIN"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = if (step == 1 || mode == "verify") pin else confirmPin,
                onValueChange = {
                    if (it.length <= 6) {
                        if (step == 1 || mode == "verify") pin = it else confirmPin = it
                        error = null
                    }
                },
                label = { Text("PIN") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                isError = error != null,
                supportingText = { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green400,
                    focusedLabelColor = Green400
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    when (mode) {
                        "verify" -> {
                            if (pin == pinManager.getPin()) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Pin.route) { inclusive = true }
                                }
                            } else {
                                error = "Incorrect PIN"
                                pin = ""
                            }
                        }
                        "setup" -> {
                            if (step == 1) {
                                if (pin.length < 4) error = "PIN must be at least 4 digits"
                                else step = 2
                            } else {
                                if (pin == confirmPin) {
                                    pinManager.savePin(pin)
                                    navController.popBackStack()
                                } else {
                                    error = "PINs do not match"
                                    confirmPin = ""
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green400)
            ) {
                Text(
                    if (mode == "verify") "Unlock" else if (step == 1) "Next" else "Confirm",
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}