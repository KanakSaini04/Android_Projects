package com.lensora.presentation.camera.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lensora.core.ui.theme.*
import com.lensora.domain.model.CameraMode

@Composable
fun CameraBottomBar(
    modifier: Modifier = Modifier,
    onCapture: () -> Unit,
    onFlipCamera: () -> Unit,
    onGalleryClick: () -> Unit,
    currentMode: CameraMode, // Corrected from String to CameraMode
    onModeChange: (CameraMode) -> Unit,
    lastPhotoUri: Uri?,
    isPoseOverlayEnabled: Boolean,
    isCompositionGuideEnabled: Boolean,
    onPoseToggle: () -> Unit,
    onCompositionToggle: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Toggle row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            ToggleChip(
                label = "Pose",
                enabled = isPoseOverlayEnabled,
                onClick = onPoseToggle
            )
            ToggleChip(
                label = "Grid",
                enabled = isCompositionGuideEnabled,
                onClick = onCompositionToggle
            )
        }

        // Mode selector
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            items(CameraMode.values()) { mode ->
                ModeChip(
                    mode = mode,
                    selected = currentMode == mode, // Resolves correctly now
                    onClick = { onModeChange(mode) }
                )
            }
        }

        // Main controls row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceGray)
                    .clickable { onGalleryClick() },
                contentAlignment = Alignment.Center
            ) {
                if (lastPhotoUri != null) {
                    AsyncImage(
                        model = lastPhotoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = WhiteDim)
                }
            }

            // Shutter button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .border(3.dp, ElectricBlue, CircleShape)
                    .padding(5.dp)
                    .background(White, CircleShape)
                    .clickable { onCapture() }
            )

            // Flip camera
            IconButton(
                onClick = onFlipCamera,
                modifier = Modifier
                    .size(52.dp)
                    .background(SurfaceGray.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    Icons.Default.FlipCameraAndroid,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun ToggleChip(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (enabled) ElectricBlueDim.copy(alpha = 0.6f) else SurfaceGray.copy(alpha = 0.5f),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (enabled) ElectricBlue else WhiteDim,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ModeChip(mode: CameraMode, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) ElectricBlue else SurfaceGray.copy(alpha = 0.5f),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(
            text = mode.name,
            color = if (selected) Black else WhiteDim,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}