package com.clustr.app.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clustr.app.auth.AuthViewModel
import com.clustr.app.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state by viewModel.state.collectAsState()
    var isSignUp by remember { mutableStateOf(false) }

    // Google Sign-In launcher
    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.signInWithGoogle(account)
            } catch (_: ApiException) {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            // ── Brand mark ──────────────────────────────────────────────────
            Text(
                "CLUSTR",
                style = MaterialTheme.typography.displayLarge.copy(
                    letterSpacing = 8.sp,
                    color = TextPrimary
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Acoustic Data Mapper",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    letterSpacing = 2.sp
                )
            )

            Spacer(Modifier.height(40.dp))

            // ── Tab toggle ───────────────────────────────────────────────────
            AuthTabToggle(
                isSignUp = isSignUp,
                onToggle = { isSignUp = it }
            )

            Spacer(Modifier.height(20.dp))

            AnimatedContent(
                targetState = isSignUp,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { signUp ->
                if (signUp) {
                    SignUpForm(
                        isLoading = state.isLoading,
                        onSignUp  = viewModel::signUp
                    )
                } else {
                    SignInForm(
                        isLoading = state.isLoading,
                        onSignIn  = viewModel::signIn
                    )
                }
            }

            // ── Error message ────────────────────────────────────────────────
            AnimatedVisibility(visible = state.errorMessage != null) {
                state.errorMessage?.let { err ->
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Divider ──────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Surface500, thickness = 0.5.dp)
                Text(
                    "  or  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                Divider(modifier = Modifier.weight(1f), color = Surface500, thickness = 0.5.dp)
            }

            Spacer(Modifier.height(20.dp))

            // ── Google button ────────────────────────────────────────────────
            GoogleSignInButton(
                isLoading = state.isLoading,
                onClick = { googleLauncher.launch(viewModel.googleSignInClient.signInIntent) }
            )

            Spacer(Modifier.height(40.dp))

            Text(
                "By continuing, you agree to our Terms of Service\nand Privacy Policy",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextTertiary,
                    fontSize = 11.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
        }

        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Accent, modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Composable
fun AuthTabToggle(isSignUp: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(Surface700, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        listOf("Sign In" to false, "Create Account" to true).forEach { (label, value) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isSignUp == value) Surface500 else Color.Transparent,
                        RoundedCornerShape(9.dp)
                    )
                    .clickable { onToggle(value) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = if (isSignUp == value) TextPrimary else TextSecondary,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SignInForm(isLoading: Boolean, onSignIn: (String, String) -> Unit) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    val focus    = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ClustrTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onNext = { focus.moveFocus(FocusDirection.Down) }
        )
        ClustrTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            showPassword = showPass,
            onTogglePassword = { showPass = !showPass },
            imeAction = ImeAction.Done,
            onDone = { if (!isLoading) onSignIn(email, password) }
        )
        Spacer(Modifier.height(4.dp))
        ClustrPrimaryButton(
            text = "Sign In",
            isLoading = isLoading,
            onClick = { onSignIn(email, password) }
        )
    }
}

@Composable
fun SignUpForm(isLoading: Boolean, onSignUp: (String, String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    val focus    = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ClustrTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            imeAction = ImeAction.Next,
            onNext = { focus.moveFocus(FocusDirection.Down) }
        )
        ClustrTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onNext = { focus.moveFocus(FocusDirection.Down) }
        )
        ClustrTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            showPassword = showPass,
            onTogglePassword = { showPass = !showPass },
            imeAction = ImeAction.Done,
            onDone = { if (!isLoading) onSignUp(username, email, password) }
        )
        Spacer(Modifier.height(4.dp))
        ClustrPrimaryButton(
            text = "Create Account",
            isLoading = isLoading,
            onClick = { onSignUp(username, email, password) }
        )
    }
}

// ── Reusable UI Components ─────────────────────────────────────────────────────

@Composable
fun ClustrTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onNext: () -> Unit = {},
    onDone: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Accent,
            unfocusedBorderColor = Surface500,
            focusedLabelColor    = Accent,
            unfocusedLabelColor  = TextSecondary,
            cursorColor          = Accent,
            focusedTextColor     = TextPrimary,
            unfocusedTextColor   = TextPrimary,
            focusedContainerColor   = Surface800,
            unfocusedContainerColor = Surface800
        ),
        visualTransformation = if (isPassword && !showPassword)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction    = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() },
            onDone = { onDone() }
        ),
        trailingIcon = if (isPassword) {{
            IconButton(onClick = onTogglePassword) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }} else null
    )
}

@Composable
fun ClustrPrimaryButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = white,
            contentColor   = Black
        )
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = Black
            )
        )
    }
}

@Composable
fun GoogleSignInButton(isLoading: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, Surface500),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface800
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Google "G" text mark (replace with actual Google icon drawable if available)
            Text(
                "G",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF4285F4),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            )
            Text(
                "Continue with Google",
                style = MaterialTheme.typography.labelLarge.copy(color = TextPrimary)
            )
        }
    }
}