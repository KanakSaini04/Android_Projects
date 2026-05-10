package com.codexcraft.lensora.ui.camera

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codexcraft.lensora.core.theme.*
import com.codexcraft.lensora.core.util.HapticUtil
import com.codexcraft.lensora.domain.usecase.AiCameraMode

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val aiMode by viewModel.aiMode.collectAsStateWithLifecycle()
    val isLensFront by viewModel.isLensFacingFront.collectAsStateWithLifecycle()
    val inferenceTime by viewModel.inferenceTimeMs.collectAsStateWithLifecycle()

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    // Bind camera whenever lens changes
    LaunchedEffect(isLensFront) {
        viewModel.bindCamera(context, lifecycleOwner, previewView)
    }

    // Shutter "breathe" spring animation
    val infiniteTransition = rememberInfiniteTransition(label = "shutter_breathe")
    val shutterScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shutter_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack)
    ) {
        // 9:16 Camera Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(0.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            HapticUtil.strongTick(context)
                            viewModel.flipCamera()
                        }
                    )
                }
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // AI Mode indicator overlay (top-left corner)
            AiModeIndicator(
                mode = aiMode,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )

            // Pose overlay hint (top-right)
            DirectorVoiceHint(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )

            // MediaPipe pose skeleton overlay (mock blue lines)
            PoseSkeletonOverlay(
                modifier = Modifier.fillMaxSize()
            )

            // Scan line — subtle 9:16 frame indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, ElectricBlueAlpha20)
            )
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AI mode text label
            Text(
                text = "${aiMode.emoji}  ${aiMode.label.uppercase()}",
                style = LensoraTypography.labelLarge.copy(
                    letterSpacing = 3.sp,
                    color = ElectricBlue
                )
            )

            Spacer(Modifier.height(20.dp))

            // Shutter row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flip button
                IconButton(
                    onClick = {
                        HapticUtil.strongTick(context)
                        viewModel.flipCamera()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(SurfaceCard, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FlipCameraAndroid,
                        contentDescription = "Flip Camera",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Shutter button with spring breathe
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(shutterScale),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(Color.Transparent, CircleShape)
                            .border(3.dp, ElectricBlue, CircleShape)
                    )
                    IconButton(
                        onClick = {
                            HapticUtil.lightClick(context)
                            viewModel.capturePhoto(context)
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .background(ElectricBlue, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Camera,
                            contentDescription = "Capture",
                            tint = MatteBlack,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Placeholder for gallery thumbnail
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SurfaceCard, RoundedCornerShape(8.dp))
                        .border(1.dp, ElectricBlueAlpha20, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AiModeIndicator(mode: AiCameraMode, modifier: Modifier = Modifier) {
    val accentColor by animateColorAsState(
        targetValue = when (mode) {
            AiCameraMode.PORTRAIT -> Color(0xFFFF6584)
            AiCameraMode.NIGHT -> Color(0xFF7C4DFF)
            AiCameraMode.MACRO -> Color(0xFF00E676)
            AiCameraMode.LANDSCAPE -> Color(0xFFFFAB40)
            else -> ElectricBlue
        },
        animationSpec = tween(600),
        label = "mode_color"
    )

    Row(
        modifier = modifier
            .background(MatteBlack.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pulsing dot
        val pulseAnim = rememberInfiniteTransition(label = "pulse")
        val pulseAlpha by pulseAnim.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "pulse_alpha"
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(accentColor.copy(alpha = pulseAlpha), CircleShape)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "AI · ${mode.label}",
            style = LensoraTypography.labelSmall.copy(color = accentColor, fontSize = 10.sp)
        )
    }
}

@Composable
private fun DirectorVoiceHint(modifier: Modifier = Modifier) {
    var hint by remember {
        mutableStateOf(directorHints.random())
    }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(12_000L)
            hint = directorHints.random()
        }
    }

    Box(
        modifier = modifier
            .background(MatteBlack.copy(0.65f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "🎬 $hint",
            style = LensoraTypography.labelSmall.copy(
                color = ElectricBlue,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun PoseSkeletonOverlay(modifier: Modifier = Modifier) {
    // Mock skeleton overlay using Canvas
    // In production, feed MediaPipe PoseLandmarker results here
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Only draw if hypothetical person detected (mock: always show faint skeleton)
        val paint = androidx.compose.ui.graphics.Paint().apply {
            color = ElectricBlue.copy(alpha = 0.15f)
            strokeWidth = 2.dp.toPx()
        }

        // Mock torso
        val cx = w * 0.5f
        val headY = h * 0.22f
        val shoulderY = h * 0.30f
        val hipY = h * 0.52f

        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx - 60f, shoulderY), androidx.compose.ui.geometry.Offset(cx + 60f, shoulderY), 2.dp.toPx())
        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx - 40f, hipY), androidx.compose.ui.geometry.Offset(cx + 40f, hipY), 2.dp.toPx())
        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx, shoulderY), androidx.compose.ui.geometry.Offset(cx, hipY), 2.dp.toPx())
        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx - 60f, shoulderY), androidx.compose.ui.geometry.Offset(cx - 40f, hipY), 2.dp.toPx())
        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx + 60f, shoulderY), androidx.compose.ui.geometry.Offset(cx + 40f, hipY), 2.dp.toPx())
        // Arms
        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx - 60f, shoulderY), androidx.compose.ui.geometry.Offset(cx - 100f, shoulderY + 80f), 2.dp.toPx())
        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx + 60f, shoulderY), androidx.compose.ui.geometry.Offset(cx + 100f, shoulderY + 80f), 2.dp.toPx())
        // Legs
        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx - 40f, hipY), androidx.compose.ui.geometry.Offset(cx - 50f, hipY + 100f), 2.dp.toPx())
        drawLine(ElectricBlue.copy(0.15f), androidx.compose.ui.geometry.Offset(cx + 40f, hipY), androidx.compose.ui.geometry.Offset(cx + 50f, hipY + 100f), 2.dp.toPx())
    }
}

private val directorHints = listOf(
    "Tilt chin slightly down",
    "Move left — better light",
    "Try a 45° angle",
    "Step back 2 feet",
    "Look just past the lens",
    "Relax your shoulders",
    "Golden hour alignment ✓"
)