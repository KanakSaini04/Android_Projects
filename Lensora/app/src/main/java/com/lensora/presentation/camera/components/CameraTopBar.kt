package com.lensora.presentation.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lensora.core.ui.theme.*

@Composable
fun CameraTopBar(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean,
    isHdrOn: Boolean,
    onFlashToggle: () -> Unit,
    onHdrToggle: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flash
        CameraIconButton(
            icon = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
            tint = if (isFlashOn) ElectricBlue else WhiteDim,
            onClick = onFlashToggle
        )

        // HDR
        Box(
            modifier = Modifier
                .background(
                    if (isHdrOn) ElectricBlueDim.copy(alpha = 0.5f) else SurfaceGray.copy(alpha = 0.5f),
                    CircleShape
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "HDR",
                color = if (isHdrOn) ElectricBlue else WhiteDim,
                fontSize = 12.sp
            )
        }

        // Settings
        CameraIconButton(
            icon = Icons.Default.Settings,
            tint = WhiteDim,
            onClick = onSettingsClick
        )
    }
}

@Composable
fun CameraIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .background(Black.copy(alpha = 0.4f), CircleShape)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}
