package com.clustr.app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clustr.app.auth.AuthViewModel
import com.clustr.app.data.model.User
import com.clustr.app.ui.theme.*

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit
) {
    val authState by authViewModel.state.collectAsState()
    val user = authState.user
    var showSignOutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ─────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Settings",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary, // Fixed visibility
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Rounded.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        }

        HorizontalDivider(color = Divider, thickness = 0.5.dp)
        Spacer(Modifier.height(8.dp))

        // ── Profile card ───────────────────────────────────────────────────────
        if (user != null) ProfileCard(user)

        // ── Audio section ──────────────────────────────────────────────────────
        SettingsSection(title = "Audio") {
            ToggleRow(
                icon  = Icons.Rounded.Mic,
                label = "Microphone",
                sublabel = "Enable live acoustic input",
                checked  = user?.micEnabled ?: true,
                onToggle = { enabled ->
                    user?.uid?.let { authViewModel.updateMicEnabled(it, enabled) }
                }
            )
        }

        // ── Security section ───────────────────────────────────────────────────
        SettingsSection(title = "Security") {
            ToggleRow(
                icon  = Icons.Rounded.Fingerprint,
                label = "Biometric Lock",
                sublabel = "Require fingerprint to open",
                checked  = user?.biometricEnabled ?: false,
                onToggle = { enabled ->
                    user?.uid?.let { authViewModel.updateBiometricEnabled(it, enabled) }
                }
            )
        }

        // ── About section ──────────────────────────────────────────────────────
        SettingsSection(title = "About") {
            LinkRow(icon = Icons.Rounded.Shield,         label = "Privacy Policy",      url = "https://clustr.app/privacy")
            HorizontalDivider(color = Divider, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            LinkRow(icon = Icons.Rounded.Description,    label = "Terms & Conditions",  url = "https://clustr.app/terms")
            HorizontalDivider(color = Divider, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            LinkRow(icon = Icons.Rounded.Info,           label = "Version",             trailingText = "1.0.0")
        }

        // ── Account section ────────────────────────────────────────────────────
        SettingsSection(title = "Account") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSignOutDialog = true }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(Icons.Rounded.Logout, contentDescription = null, tint = AccentRed, modifier = Modifier.size(20.dp))
                Text("Sign Out", style = MaterialTheme.typography.labelLarge.copy(color = AccentRed))
            }
        }

        Spacer(Modifier.height(32.dp))

        // Branding footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("CLUSTR", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 4.sp, color = TextTertiary)
            Spacer(Modifier.height(4.dp))
            Text("Acoustic Data Mapper", fontFamily = FontFamily.Monospace, fontSize = 9.sp, letterSpacing = 1.sp, color = TextTertiary)
        }

        Spacer(Modifier.height(24.dp).navigationBarsPadding())
    }

    // Sign out confirmation
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor   = Surface700,
            tonalElevation   = 0.dp,
            title  = { Text("Sign Out?", color = TextPrimary) },
            text   = { Text("You'll need to sign in again to access your recordings.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    authViewModel.signOut()
                    onSignOut()
                }) { Text("Sign Out", color = AccentRed) }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

// ── Sub-components ─────────────────────────────────────────────────────────────

@Composable
fun ProfileCard(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface800)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(AccentDim),
            contentAlignment = Alignment.Center
        ) {
            Text(
                user.username.take(2).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Accent
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(user.username, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(user.email, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, color = TextSecondary))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            title.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Surface800),
            content = content
        )
    }
}

@Composable
fun ToggleRow(
    icon: ImageVector,
    label: String,
    sublabel: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
            Text(sublabel, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextSecondary))
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor  = Black,
                checkedTrackColor  = Accent,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Surface500
            )
        )
    }
}

@Composable
fun LinkRow(
    icon: ImageVector,
    label: String,
    url: String? = null,
    trailingText: String? = null
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (url != null) Modifier.clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else Modifier
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), color = TextPrimary)
        if (trailingText != null) {
            Text(trailingText, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        } else if (url != null) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        }
    }
}