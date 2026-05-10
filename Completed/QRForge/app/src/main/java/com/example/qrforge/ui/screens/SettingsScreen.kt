package com.example.qrforge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.qrforge.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground)
            Text("Customize QRForge", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(0.4f))
        }
// Profile Card
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        var showAuth by remember { mutableStateOf(false) }

        Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            ProfileCard(onSignOut = {
                auth.signOut()
                showAuth = true
            })
        }

        if (showAuth) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = {},
                properties = androidx.compose.ui.window.DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                AuthScreen(onAuthComplete = { showAuth = false })
            }
        }
        // Appearance
        SettingsSection("Appearance") {
            SettingsToggleRow(
                icon = Icons.Outlined.DarkMode,
                title = "Dark Theme",
                subtitle = "Switch between light and dark",
                checked = settings.isDarkTheme,
                onToggle = { viewModel.setDarkTheme(it) }
            )
        }

        // Security
        SettingsSection("Security") {
            SettingsToggleRow(
                icon = Icons.Outlined.Fingerprint,
                title = "Biometric Lock",
                subtitle = "Require fingerprint or face unlock",
                checked = settings.biometricLock,
                onToggle = { viewModel.setBiometricLock(it) }
            )
        }

        // Scanning
        SettingsSection("Scanning") {
            SettingsToggleRow(
                icon = Icons.Outlined.OpenInBrowser,
                title = "Auto-Open URLs",
                subtitle = "Automatically open detected links",
                checked = settings.autoOpenUrls,
                onToggle = { viewModel.setAutoOpenUrls(it) }
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            SettingsToggleRow(
                icon = Icons.Outlined.VolumeUp,
                title = "Beep on Scan",
                subtitle = "Play sound when QR is detected",
                checked = settings.beepOnScan,
                onToggle = { viewModel.setBeepOnScan(it) }
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            SettingsToggleRow(
                icon = Icons.Outlined.Vibration,
                title = "Vibration",
                subtitle = "Haptic feedback on scan",
                checked = settings.vibrationOnScan,
                onToggle = { viewModel.setVibration(it) }
            )
        }

        // Export
        SettingsSection("Export") {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.HighQuality, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Default QR Size",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground)
                        Text("Export resolution for generated QR codes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(256, 512, 1024).forEach { size ->
                        FilterChip(
                            selected = settings.qrSize == size,
                            onClick = { viewModel.setQrSize(size) },
                            label = { Text("${size}px",
                                style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        // About
        SettingsSection("About") {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.WifiOff, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Fully Offline",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground)
                        Text("No internet required — all processing is on-device",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Info, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("QRForge v1.0.0",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground)
                        Text("Craft. Scan. Connect.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    }
                }
            }
        }

        // Legal
        SettingsSection("Legal") {
            Column {
                LegalRow(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "How we handle your data",
                    onClick = { showPrivacyPolicy = true }
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                LegalRow(
                    icon = Icons.Outlined.Gavel,
                    title = "Terms of Service",
                    subtitle = "Rules and conditions of use",
                    onClick = { showTerms = true }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicy) {
        LegalDialog(
            title = "Privacy Policy",
            content = PRIVACY_POLICY,
            onDismiss = { showPrivacyPolicy = false }
        )
    }

    // Terms Dialog
    if (showTerms) {
        LegalDialog(
            title = "Terms of Service",
            content = TERMS_OF_SERVICE,
            onDismiss = { showTerms = false }
        )
    }
}

@Composable
fun LegalRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(0.45f))
        }
        Icon(Icons.Outlined.ChevronRight, null,
            tint = MaterialTheme.colorScheme.onBackground.copy(0.3f),
            modifier = Modifier.size(20.dp))
    }
}

@Composable
fun LegalDialog(title: String, content: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.fillMaxSize()) {
                // Title bar
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

                // Scrollable content
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(content, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column { content() }
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(0.45f))
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// ─── Legal Content ───────────────────────────────────────

val PRIVACY_POLICY = """
PRIVACY POLICY
Last Updated: May 2026

1. INTRODUCTION

QRForge ("we", "our", or "the app") is committed to protecting your privacy. This Privacy Policy explains how QRForge handles information when you use our application.

2. DATA WE DO NOT COLLECT

QRForge is a fully offline application. We do not collect, transmit, store, or share any personal data with external servers or third parties. Specifically:

- We do not collect your name, email, or contact information
- We do not track your location
- We do not use analytics or advertising SDKs
- We do not send any data over the internet
- We do not create user accounts

3. DATA STORED LOCALLY ON YOUR DEVICE

QRForge stores the following data locally on your device only:

- Scan History: QR codes and barcodes you have scanned or generated are stored in a local database on your device. This data never leaves your device.
- App Settings: Your preferences such as theme, biometric lock, and scan settings are stored locally using Android DataStore.

You can delete your scan history at any time from the History screen. Uninstalling the app permanently removes all locally stored data.

4. CAMERA PERMISSION

QRForge requests camera access solely to scan QR codes and barcodes. Camera feed is processed entirely on-device using Google ML Kit's offline barcode scanning model. No images or video are stored or transmitted.

5. STORAGE PERMISSION

Storage permission is requested only when you choose to save a generated QR code image to your device gallery. No other files are accessed.

6. BIOMETRIC PERMISSION

If you enable biometric lock, QRForge uses Android's BiometricPrompt API. Biometric data (fingerprint, face) is handled entirely by your device's secure hardware and Android OS. QRForge never accesses or stores biometric data.

7. THIRD-PARTY LIBRARIES

QRForge uses the following on-device libraries:
- Google ML Kit Barcode Scanning (offline model)
- ZXing (QR code generation)
- Jetpack Compose & AndroidX libraries

None of these libraries transmit data externally when used in QRForge.

8. CHILDREN'S PRIVACY

QRForge does not knowingly collect any information from children under 13. The app contains no user accounts, no data collection, and no internet connectivity.

9. CHANGES TO THIS POLICY

We may update this Privacy Policy from time to time. Any changes will be reflected within the app. Continued use of QRForge after changes constitutes acceptance of the updated policy.

10. CONTACT

If you have any questions about this Privacy Policy, please contact us through the app store listing.
""".trimIndent()

val TERMS_OF_SERVICE = """
TERMS OF SERVICE
Last Updated: May 2026

1. ACCEPTANCE OF TERMS

By downloading and using QRForge, you agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use the app.

2. DESCRIPTION OF SERVICE

QRForge is a mobile application that allows users to:
- Scan QR codes and barcodes using the device camera or gallery images
- Generate QR codes for various data types including URLs, text, WiFi credentials, contact information, email, SMS, and phone numbers
- View and manage scan history
- Customize QR code appearance

3. ACCEPTABLE USE

You agree to use QRForge only for lawful purposes. You must not use the app to:

- Generate QR codes containing malicious content, malware, or phishing links
- Scan or generate QR codes for fraudulent or illegal activities
- Violate any applicable local, national, or international laws
- Infringe on the intellectual property rights of others
- Generate QR codes that direct others to harmful or illegal content

4. QR CODE RESPONSIBILITY

You are solely responsible for the content of QR codes you generate using QRForge. The app is a tool and does not endorse, verify, or take responsibility for the content encoded in any QR code.

When scanning QR codes, always exercise caution before opening URLs or following instructions from unknown QR codes, as malicious QR codes may direct you to harmful content.

5. CAMERA AND DEVICE PERMISSIONS

QRForge requests device permissions (camera, storage, biometric) solely for the features described in this app. Granting permissions is voluntary, though some features may be unavailable without them.

6. OFFLINE FUNCTIONALITY

QRForge is designed to work entirely offline. We do not guarantee uninterrupted availability of any online resources that scanned QR codes may link to.

7. NO WARRANTIES

QRForge is provided "as is" without warranty of any kind, express or implied, including but not limited to:

- Accuracy of QR code scanning results
- Compatibility with all device models
- Uninterrupted or error-free operation
- Fitness for a particular purpose

8. LIMITATION OF LIABILITY

To the maximum extent permitted by law, QRForge and its developers shall not be liable for:

- Any damages arising from use or inability to use the app
- Any content accessed through scanned QR codes
- Data loss due to app malfunction or device issues
- Any indirect, incidental, or consequential damages

9. INTELLECTUAL PROPERTY

QRForge, including its design, code, and branding, is the intellectual property of its developers. You may not copy, modify, distribute, or reverse engineer the app without explicit permission.

10. MODIFICATIONS TO TERMS

We reserve the right to modify these Terms of Service at any time. Updated terms will be made available within the app. Your continued use of QRForge after changes are posted constitutes your acceptance of the new terms.

11. TERMINATION

We reserve the right to discontinue QRForge at any time without notice. You may stop using the app at any time by uninstalling it from your device.

12. GOVERNING LAW

These Terms shall be governed by and construed in accordance with applicable laws. Any disputes arising from these terms shall be resolved through good-faith negotiation.

13. CONTACT

For questions regarding these Terms of Service, please contact us through the app store listing.
""".trimIndent()