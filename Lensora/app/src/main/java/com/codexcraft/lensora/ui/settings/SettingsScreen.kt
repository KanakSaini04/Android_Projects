package com.codexcraft.lensora.ui.settings

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.codexcraft.lensora.BuildConfig
import com.codexcraft.lensora.core.theme.*
import com.codexcraft.lensora.core.util.Constants
import com.codexcraft.lensora.ui.camera.CameraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    deepLinkPath: String? = null,
    viewModel: SettingsViewModel = hiltViewModel(),
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isMirrorSync by viewModel.isMirrorSync.collectAsStateWithLifecycle()
    val isFind by viewModel.isFind.collectAsStateWithLifecycle()
    val aiMode by cameraViewModel.aiMode.collectAsStateWithLifecycle()
    val inferenceTime by cameraViewModel.inferenceTimeMs.collectAsStateWithLifecycle()
    val modeSwitches by cameraViewModel.modeSwitchCount.collectAsStateWithLifecycle()

    var showPrivacySheet by remember { mutableStateOf(false) }
    var showTermsSheet by remember { mutableStateOf(false) }

    // Handle deep-link to open correct sheet
    LaunchedEffect(deepLinkPath) {
        when {
            deepLinkPath?.contains("privacy") == true -> showPrivacySheet = true
            deepLinkPath?.contains("terms") == true -> showTermsSheet = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack)
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "THE ENGINE",
                style = LensoraTypography.labelLarge.copy(letterSpacing = 4.sp)
            )
            Spacer(Modifier.width(12.dp))
            Box(Modifier.height(1.dp).weight(1f).background(ElectricBlueAlpha20))
        }

        Spacer(Modifier.height(28.dp))

        // Profile Header
        ProfileHeader(
            name = userProfile.name,
            email = userProfile.email,
            photoUrl = userProfile.photoUrl
        )

        Spacer(Modifier.height(28.dp))
        SectionDivider("LIVE TELEMETRY")
        Spacer(Modifier.height(12.dp))

        // Telemetry dashboard
        TelemetryCard(
            currentMode = aiMode.label,
            inferenceTimeMs = inferenceTime,
            modeSwitches = modeSwitches
        )

        Spacer(Modifier.height(28.dp))
        SectionDivider("CONNECTIVITY")
        Spacer(Modifier.height(12.dp))

        // Mirror Sync toggle
        SettingsToggleRow(
            icon = Icons.Outlined.Wifi,
            title = "Mirror Sync",
            subtitle = "Stream to desktop via local Ktor server + QR",
            checked = isMirrorSync,
            onCheckedChange = { viewModel.setMirrorSync(it) }
        )
        Spacer(Modifier.height(12.dp))

        // Find toggle
        SettingsToggleRow(
            icon = Icons.Outlined.Search,
            title = "Find",
            subtitle = "Offline-first landmark & visual search",
            checked = isFind,
            onCheckedChange = { viewModel.setFind(it) }
        )

        Spacer(Modifier.height(28.dp))
        SectionDivider("ACCOUNT")
        Spacer(Modifier.height(12.dp))

        SettingsActionRow(
            icon = Icons.Outlined.Logout,
            title = "Sign Out",
            tint = DangerRed,
            onClick = { viewModel.signOut() }
        )

        Spacer(Modifier.height(28.dp))
        SectionDivider("LEGAL")
        Spacer(Modifier.height(12.dp))

        SettingsActionRow(
            icon = Icons.Outlined.PrivacyTip,
            title = "Privacy Policy",
            onClick = { showPrivacySheet = true }
        )
        Spacer(Modifier.height(12.dp))
        SettingsActionRow(
            icon = Icons.Outlined.Gavel,
            title = "Terms & Conditions",
            onClick = { showTermsSheet = true }
        )

        Spacer(Modifier.height(40.dp))

        // Version footer
        Text(
            text = "Lensora AI v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) · CodexCraft",
            style = LensoraTypography.labelSmall.copy(color = TextMuted, fontSize = 10.sp),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
    }

    // Legal Bottom Sheets
    if (showPrivacySheet) {
        LegalBottomSheet(
            title = "Privacy Policy",
            body = Constants.PRIVACY_POLICY_TEXT,
            onDismiss = { showPrivacySheet = false }
        )
    }

    if (showTermsSheet) {
        LegalBottomSheet(
            title = "Terms & Conditions",
            body = Constants.TERMS_AND_CONDITIONS_TEXT,
            onDismiss = { showTermsSheet = false }
        )
    }
}

@Composable
private fun ProfileHeader(name: String, email: String, photoUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(12.dp))
            .border(1.dp, ElectricBlueAlpha20, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular profile image
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(2.dp, ElectricBlue, CircleShape)
                .background(SurfaceElevated, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl.isNotEmpty()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "L",
                    style = LensoraTypography.titleLarge.copy(color = ElectricBlue)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = name.ifEmpty { "Lensora User" },
                style = LensoraTypography.titleSmall
            )
            Text(
                text = email.ifEmpty { "–" },
                style = LensoraTypography.bodyMedium
            )
        }
    }
}

@Composable
private fun TelemetryCard(
    currentMode: String,
    inferenceTimeMs: Long,
    modeSwitches: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ElectricBlue.copy(0.4f), RoundedCornerShape(12.dp))
            .background(ElectricBlueAlpha10, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Pulsing indicator
            val infiniteTransition = rememberInfiniteTransition(label = "tele")
            val pulseAlpha by infiniteTransition.animateFloat(
                0.4f, 1f,
                infiniteRepeatable(androidx.compose.animation.core.tween(800), androidx.compose.animation.core.RepeatMode.Reverse),
                label = "tele_pulse"
            )
            Box(
                Modifier
                    .size(7.dp)
                    .background(ElectricBlue.copy(alpha = pulseAlpha), CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "LIVE",
                style = LensoraTypography.labelSmall.copy(
                    color = ElectricBlue,
                    letterSpacing = 2.sp
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        TelemetryRow("Current AI Mode", currentMode)
        TelemetryRow("Inference Time", "${inferenceTimeMs} ms")
        TelemetryRow("Mode Switches", "$modeSwitches this session")
    }
}

@Composable
private fun TelemetryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = LensoraTypography.bodyMedium)
        Text(
            value,
            style = LensoraTypography.titleSmall.copy(color = ElectricBlue)
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(10.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = ElectricBlue, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = LensoraTypography.titleSmall)
            Text(subtitle, style = LensoraTypography.bodyMedium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MatteBlack,
                checkedTrackColor = ElectricBlue,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceElevated
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    tint: Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Text(
            title,
            style = LensoraTypography.titleSmall.copy(color = tint),
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SectionDivider(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            label,
            style = LensoraTypography.labelSmall.copy(
                color = TextMuted,
                letterSpacing = 2.sp,
                fontSize = 10.sp
            )
        )
        Spacer(Modifier.width(10.dp))
        Box(Modifier.height(1.dp).weight(1f).background(TextMuted.copy(0.2f)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegalBottomSheet(
    title: String,
    body: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 8.dp)
                    .width(40.dp)
                    .height(3.dp)
                    .background(TextMuted, RoundedCornerShape(2.dp))
            )
        },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = title.uppercase(),
                style = LensoraTypography.labelLarge.copy(letterSpacing = 3.sp)
            )
            Spacer(Modifier.height(4.dp))
            Box(Modifier.width(40.dp).height(1.dp).background(ElectricBlue))
            Spacer(Modifier.height(20.dp))
            Text(
                text = body,
                style = LensoraTypography.bodyMedium.copy(
                    lineHeight = androidx.compose.ui.unit.TextUnit(22f, androidx.compose.ui.unit.TextUnitType.Sp)
                )
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}