package com.codexcraft.lensora.ui.permissions

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codexcraft.lensora.core.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
data class PermissionItem(
    val permission: String,
    val icon: ImageVector,
    val title: String,
    val description: String
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(onPermissionsGranted: () -> Unit) {
    val permissions = listOf(
        PermissionItem(
            Manifest.permission.CAMERA,
            Icons.Outlined.CameraAlt,
            "Camera",
            "To power the Sentient Viewfinder and AI scene detection."
        ),
        PermissionItem(
            Manifest.permission.RECORD_AUDIO,
            Icons.Outlined.Mic,
            "Microphone",
            "To capture pristine audio with your videos."
        ),
        PermissionItem(
            Manifest.permission.READ_MEDIA_IMAGES,
            Icons.Outlined.Photo,
            "Media Access",
            "To secure and access your captured photos in the Vault."
        )
    )

    val permissionState = rememberMultiplePermissionsState(
        permissions = permissions.map { it.permission }
    )

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            onPermissionsGranted()
        }
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
                Spacer(Modifier.height(60.dp))

                Text(
                    text = "ACCESS REQUIRED",
                    style = LensoraTypography.labelLarge.copy(letterSpacing = 4.sp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Lensora AI needs the following\npermissions to function.",
                    style = LensoraTypography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(48.dp))

                permissions.forEach { item ->
                    PermissionCard(item = item)
                    Spacer(Modifier.height(16.dp))
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = { permissionState.launchMultiplePermissionRequest() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        contentColor = MatteBlack
                    )
                ) {
                    Text(
                        text = "GRANT ACCESS",
                        style = LensoraTypography.labelLarge.copy(
                            color = MatteBlack,
                            letterSpacing = 3.sp
                        )
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PermissionCard(item: PermissionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(8.dp))
            .border(1.dp, ElectricBlueAlpha20, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = ElectricBlue,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = item.title,
                style = LensoraTypography.titleSmall
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.description,
                style = LensoraTypography.bodyMedium
            )
        }
    }
}