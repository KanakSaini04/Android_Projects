package com.codexcraft.lensora.ui.auth

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codexcraft.lensora.core.theme.*

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Navigate on success
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) onAuthSuccess()
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack)
            .systemBarsPadding()
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(0.8f))

                // ── Brand ────────────────────────────────────────────────
                Text(
                    text = "LENSORA AI",
                    style = LensoraTypography.displayMedium.copy(
                        letterSpacing = 4.sp,
                        color = TextPrimary
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Your Sentient Viewfinder",
                    style = LensoraTypography.bodyMedium.copy(
                        color = TextSecondary,
                        letterSpacing = 2.sp
                    )
                )

                Spacer(Modifier.height(40.dp))
                Divider(color = ElectricBlueAlpha20, thickness = 1.dp)
                Spacer(Modifier.height(32.dp))

                // ── Name field ───────────────────────────────────────────
                LensoraTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Your Name",
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(Modifier.height(12.dp))

                // ── Email field ──────────────────────────────────────────
                LensoraTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email Address",
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Email,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.signInWithEmail(name, email)
                        }
                    )
                )

                // ── Error message ────────────────────────────────────────
                AnimatedVisibility(visible = authState is AuthState.Error) {
                    Column {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = (authState as? AuthState.Error)?.message ?: "",
                            style = LensoraTypography.bodySmall.copy(color = DangerRed),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Continue button ──────────────────────────────────────
                ContinueButton(
                    isLoading = authState is AuthState.Loading,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.signInWithEmail(name, email)
                    }
                )

                Spacer(Modifier.height(24.dp))

                // ── Divider with OR ──────────────────────────────────────
                OrDivider()

                Spacer(Modifier.height(24.dp))

                // ── Google Sign-In button ────────────────────────────────
                GoogleSignInButton(
                    isLoading = authState is AuthState.Loading,
                    onClick = { viewModel.signInWithGoogle(context as Activity) }
                )

                Spacer(Modifier.weight(1f))

                // ── Legal text ───────────────────────────────────────────
                LegalText()
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

// ── Reusable text field ──────────────────────────────────────────────────────

@Composable
private fun LensoraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                placeholder,
                style = LensoraTypography.bodyMedium.copy(color = TextMuted)
            )
        },
        leadingIcon = leadingIcon,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ElectricBlue,
            unfocusedBorderColor = TextMuted.copy(alpha = 0.4f),
            focusedContainerColor = SurfaceCard,
            unfocusedContainerColor = SurfaceCard,
            cursorColor = ElectricBlue,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLeadingIconColor = ElectricBlue,
            unfocusedLeadingIconColor = TextMuted
        )
    )
}

// ── Continue button ──────────────────────────────────────────────────────────

@Composable
private fun ContinueButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElectricBlue,
            contentColor = MatteBlack,
            disabledContainerColor = ElectricBlue.copy(alpha = 0.4f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MatteBlack,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "CONTINUE",
                style = LensoraTypography.labelLarge.copy(
                    color = MatteBlack,
                    letterSpacing = 3.sp
                )
            )
        }
    }
}

// ── OR divider ───────────────────────────────────────────────────────────────

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(TextMuted.copy(alpha = 0.3f))
        )
        Text(
            text = "  OR  ",
            style = LensoraTypography.labelSmall.copy(
                color = TextMuted,
                letterSpacing = 2.sp
            )
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(TextMuted.copy(alpha = 0.3f))
        )
    }
}

// ── Google button ────────────────────────────────────────────────────────────

@Composable
private fun GoogleSignInButton(isLoading: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isLoading) TextMuted.copy(0.3f) else ElectricBlue.copy(0.6f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SurfaceCard,
            contentColor = TextPrimary,
            disabledContainerColor = SurfaceCard.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = ElectricBlue,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Google G logo
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White, RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        style = LensoraTypography.labelLarge.copy(
                            color = Color(0xFF4285F4),
                            fontSize = 13.sp
                        )
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    style = LensoraTypography.bodyMedium.copy(
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

// ── Legal annotated string ───────────────────────────────────────────────────

@Composable
private fun LegalText() {
    val annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = TextMuted, fontSize = 11.sp)) {
            append("By continuing, you agree to our ")
        }
        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(
            SpanStyle(
                color = ElectricBlue,
                fontSize = 11.sp,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium
            )
        ) {
            append("Terms & Conditions")
        }
        pop()
        withStyle(SpanStyle(color = TextMuted, fontSize = 11.sp)) {
            append(" and ")
        }
        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(
            SpanStyle(
                color = ElectricBlue,
                fontSize = 11.sp,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium
            )
        ) {
            append("Privacy Policy")
        }
        pop()
        withStyle(SpanStyle(color = TextMuted, fontSize = 11.sp)) {
            append(".")
        }
    }

    androidx.compose.foundation.text.ClickableText(
        text = annotated,
        style = LensoraTypography.bodySmall.copy(textAlign = TextAlign.Center),
        onClick = { /* Handled via App Links */ }
    )
}