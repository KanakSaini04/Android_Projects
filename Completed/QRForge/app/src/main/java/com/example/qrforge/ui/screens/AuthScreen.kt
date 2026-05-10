package com.example.qrforge.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.qrforge.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

enum class AuthState {
    AUTH,           // Sign in / Create account
    VERIFY_EMAIL,   // OTP / verify email sent
    FORGOT_PASSWORD // Forgot password sent
}

@Composable
fun AuthScreen(onAuthComplete: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Check if already signed in and verified
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            onAuthComplete()
        }
    }

    var authState by remember { mutableStateOf(AuthState.AUTH) }
    var isSignIn by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        emailError = ""; passwordError = ""; nameError = ""
        if (!isSignIn && name.isBlank()) { nameError = "Enter your name"; return false }
        if (email.isBlank() || !email.contains("@")) { emailError = "Enter a valid email"; return false }
        if (password.length < 6) { passwordError = "Password must be at least 6 characters"; return false }
        if (!isSignIn && password != confirmPassword) { passwordError = "Passwords do not match"; return false }
        return true
    }

    when (authState) {
        AuthState.VERIFY_EMAIL -> {
            VerifyEmailScreen(
                email = email,
                onResend = {
                    auth.currentUser?.sendEmailVerification()
                    Toast.makeText(context, "Verification email resent!", Toast.LENGTH_SHORT).show()
                },
                onCheckVerified = {
                    isLoading = true
                    auth.currentUser?.reload()?.addOnCompleteListener { task ->
                        isLoading = false
                        if (auth.currentUser?.isEmailVerified == true) {
                            onAuthComplete()
                        } else {
                            Toast.makeText(context, "Email not verified yet. Please check your inbox.", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onBack = {
                    auth.signOut()
                    authState = AuthState.AUTH
                },
                isLoading = isLoading
            )
            return
        }
        AuthState.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                email = email,
                onBack = { authState = AuthState.AUTH }
            )
            return
        }
        AuthState.AUTH -> { /* show below */ }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
        Box(
            Modifier.fillMaxWidth().height(320.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF0A0A0A))))
        )

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            // Logo
            Box(
                Modifier.size(80.dp).clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF4B3FC7).copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.QrCode, null,
                    tint = Color(0xFF7C6FE0), modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text("QRForge", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Craft. Scan. Connect.", fontSize = 13.sp, color = Color.White.copy(0.4f))

            Spacer(Modifier.height(40.dp))

            // Tab switcher
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1A1A2E)).padding(4.dp)
            ) {
                listOf(true to "Sign In", false to "Create Account").forEach { (signIn, label) ->
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(if (isSignIn == signIn) Color(0xFF4B3FC7) else Color.Transparent)
                            .clickable { isSignIn = signIn }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label,
                            fontWeight = if (isSignIn == signIn) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (isSignIn == signIn) Color.White else Color.White.copy(0.4f))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Name field (Create Account only)
            AnimatedVisibility(visible = !isSignIn) {
                Column(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = "" },
                        placeholder = { Text("Full Name", color = Color.White.copy(0.3f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = authFieldColors()
                    )
                    if (nameError.isNotEmpty()) {
                        Text(nameError, color = Color(0xFFFF6B6B), fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Email field
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    placeholder = { Text("Email", color = Color.White.copy(0.3f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = authFieldColors()
                )
                if (emailError.isNotEmpty()) {
                    Text(emailError, color = Color(0xFFFF6B6B), fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            // Password field
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = "" },
                    placeholder = { Text("Password", color = Color.White.copy(0.3f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                null, tint = Color.White.copy(0.4f)
                            )
                        }
                    },
                    colors = authFieldColors()
                )
                if (passwordError.isNotEmpty()) {
                    Text(passwordError, color = Color(0xFFFF6B6B), fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                }
            }

            // Confirm password
            AnimatedVisibility(visible = !isSignIn) {
                Column(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Confirm Password", color = Color.White.copy(0.3f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        visualTransformation = if (confirmVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(
                                    if (confirmVisible) Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff,
                                    null, tint = Color.White.copy(0.4f)
                                )
                            }
                        },
                        colors = authFieldColors()
                    )
                }
            }

            // Forgot password
            AnimatedVisibility(visible = isSignIn) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = {
                        if (email.isBlank() || !email.contains("@")) {
                            emailError = "Enter your email first"
                        } else {
                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    authState = AuthState.FORGOT_PASSWORD
                                }
                                .addOnFailureListener {
                                    emailError = it.message ?: "Failed to send reset email"
                                }
                        }
                    }) {
                        Text("Forgot password?", color = Color(0xFF4FFFB0), fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Main action button
            Button(
                onClick = {
                    if (!validate()) return@Button
                    isLoading = true
                    if (isSignIn) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                isLoading = false
                                val user = result.user
                                if (user?.isEmailVerified == true) {
                                    onAuthComplete()
                                } else {
                                    user?.sendEmailVerification()
                                    authState = AuthState.VERIFY_EMAIL
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                emailError = it.message ?: "Sign in failed"
                            }
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                val user = result.user
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build()
                                user?.updateProfile(profileUpdates)
                                user?.sendEmailVerification()
                                    ?.addOnSuccessListener {
                                        isLoading = false
                                        authState = AuthState.VERIFY_EMAIL
                                    }
                                    ?.addOnFailureListener {
                                        isLoading = false
                                        authState = AuthState.VERIFY_EMAIL
                                    }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                emailError = it.message ?: "Account creation failed"
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FFFB0)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFF0A0A0A), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        if (isSignIn) "Sign In" else "Create Account",
                        fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0A0A0A)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Divider
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFF2A2A3E))
                Text("  or  ", color = Color.White.copy(0.3f), fontSize = 13.sp)
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFF2A2A3E))
            }

            Spacer(Modifier.height(20.dp))

            // Google button
            OutlinedButton(
                onClick = { onAuthComplete() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF2A2A3E)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF141428))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google",
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("Continue with Google", color = Color.White,
                    fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }

            Spacer(Modifier.height(32.dp))

            // Terms
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("By continuing, you agree to our ", fontSize = 11.sp, color = Color.White.copy(0.3f))
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("Terms of Service", fontSize = 11.sp, color = Color(0xFF4FFFB0),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { showTerms = true })
                Text(" and ", fontSize = 11.sp, color = Color.White.copy(0.3f))
                Text("Privacy Policy", fontSize = 11.sp, color = Color(0xFF4FFFB0),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { showPrivacyPolicy = true })
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showPrivacyPolicy) {
        AuthLegalDialog(title = "Privacy Policy", content = PRIVACY_POLICY,
            onDismiss = { showPrivacyPolicy = false })
    }
    if (showTerms) {
        AuthLegalDialog(title = "Terms of Service", content = TERMS_OF_SERVICE,
            onDismiss = { showTerms = false })
    }
}

// ─── Verify Email Screen ─────────────────────────────────
@Composable
fun VerifyEmailScreen(
    email: String,
    onResend: () -> Unit,
    onCheckVerified: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    Box(Modifier.fillMaxSize().background(Color(0xFF0A0A0A)), contentAlignment = Alignment.Center) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                Modifier.size(90.dp).clip(CircleShape)
                    .background(Color(0xFF4FFFB0).copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.MarkEmailUnread, null,
                    tint = Color(0xFF4FFFB0), modifier = Modifier.size(48.dp))
            }

            Text("Verify Your Email", fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold, color = Color.White)

            Text(
                "We sent a verification link to\n$email\n\nPlease check your inbox and click the link to verify your account.",
                fontSize = 14.sp, color = Color.White.copy(0.5f),
                textAlign = TextAlign.Center, lineHeight = 22.sp
            )

            Button(
                onClick = onCheckVerified,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FFFB0)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFF0A0A0A),
                        modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("I've Verified My Email", fontWeight = FontWeight.Bold,
                        fontSize = 15.sp, color = Color(0xFF0A0A0A))
                }
            }

            OutlinedButton(
                onClick = onResend,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF2A2A3E)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF141428))
            ) {
                Text("Resend Verification Email", color = Color.White,
                    fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }

            TextButton(onClick = onBack) {
                Text("← Back to Sign In", color = Color.White.copy(0.4f), fontSize = 13.sp)
            }
        }
    }
}

// ─── Forgot Password Screen ──────────────────────────────
@Composable
fun ForgotPasswordScreen(email: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(0xFF0A0A0A)), contentAlignment = Alignment.Center) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                Modifier.size(90.dp).clip(CircleShape)
                    .background(Color(0xFF4B3FC7).copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.LockReset, null,
                    tint = Color(0xFF7C6FE0), modifier = Modifier.size(48.dp))
            }

            Text("Reset Email Sent", fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold, color = Color.White)

            Text(
                "We sent a password reset link to\n$email\n\nCheck your inbox and follow the instructions to reset your password.",
                fontSize = 14.sp, color = Color.White.copy(0.5f),
                textAlign = TextAlign.Center, lineHeight = 22.sp
            )

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FFFB0))
            ) {
                Text("Back to Sign In", fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = Color(0xFF0A0A0A))
            }
        }
    }
}

// ─── Profile Card ────────────────────────────────────────
@Composable
fun ProfileCard(onSignOut: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val name = user?.displayName ?: "User"
    val email = user?.email ?: ""
    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }
        .take(2).joinToString("")

    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    Modifier.size(56.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials.ifEmpty { "U" }, fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(email, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                }
                // Verified badge
                if (user?.isEmailVerified == true) {
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF27AE60).copy(0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Verified", fontSize = 10.sp,
                            color = Color(0xFF27AE60), fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

            // Sign out button
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(0.05f)
                )
            ) {
                Icon(Icons.Outlined.Logout, null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sign Out", color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Auth Field Colors ───────────────────────────────────
@Composable
fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF4FFFB0),
    unfocusedBorderColor = Color(0xFF2A2A3E),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color(0xFF4FFFB0),
    focusedContainerColor = Color(0xFF141428),
    unfocusedContainerColor = Color(0xFF141428),
)

// ─── Legal Dialog ────────────────────────────────────────
@Composable
fun AuthLegalDialog(title: String, content: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141428))
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, color = Color.White)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, null, tint = Color.White.copy(0.5f))
                    }
                }
                HorizontalDivider(color = Color(0xFF2A2A3E), thickness = 0.5.dp)
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
                ) {
                    Text(content, fontSize = 13.sp,
                        color = Color.White.copy(0.7f), lineHeight = 22.sp)
                }
            }
        }
    }
}