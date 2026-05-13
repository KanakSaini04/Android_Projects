package com.clustr.app.ui.screens.visualizer

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clustr.app.ui.theme.*

@Composable
fun VisualizerScreen(
    viewModel: VisualizerViewModel,
    micEnabled: Boolean
) {
    val state by viewModel.state.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(micEnabled) { viewModel.setMicEnabled(micEnabled) }
    LaunchedEffect(Unit) { if (micEnabled) viewModel.startListening() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        // ── Full-screen canvas ────────────────────────────────────────────────
        ClustrCanvas(
            nodes     = state.nodes,
            rotationY = state.rotationY,
            modifier  = Modifier.fillMaxSize()
        )

        // ── Top HUD ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    "CLUSTR",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 4.sp,
                    color = TextPrimary.copy(alpha = 0.9f)
                )
                Text(
                    "ACOUSTIC MAPPER",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 7.sp,
                    letterSpacing = 2.sp,
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                HudMetric(label = "FREQ", value = if (state.activeHz > 0f) "${formatFreqHud(state.activeHz)}Hz" else "---")
                HudMetric(label = "NODES", value = state.nodeCount.toString(), align = Alignment.End)
            }
        }

        // ── Recording timer badge ─────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.isRecording,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 60.dp),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            RecordingBadge(ms = state.recordingMs)
        }

        // ── Bottom controls ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // "Just saved" toast
            AnimatedVisibility(visible = state.justSaved) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AccentDim)
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Recording saved ✓",
                        color = Accent,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            // Control row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Mic status pill
                LiveStatusPill(isListening = state.isListening)

                // Record / Stop button
                RecordButton(
                    isRecording = state.isRecording,
                    onStart     = { viewModel.startRecording() },
                    onStop      = { showSaveDialog = true }
                )
            }
        }
    }

    // ── Save dialog ───────────────────────────────────────────────────────────
    if (showSaveDialog) {
        var titleText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                showSaveDialog = false
                viewModel.stopAndSaveRecording(titleText)
            },
            containerColor = Surface700,
            tonalElevation = 0.dp,
            title = {
                Text("Save Recording", style = MaterialTheme.typography.titleMedium)
            },
            text = {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    placeholder = { Text("Recording title…", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Surface500,
                        focusedTextColor     = TextPrimary,
                        unfocusedTextColor   = TextPrimary,
                        focusedContainerColor   = Surface800,
                        unfocusedContainerColor = Surface800,
                        cursorColor          = Accent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSaveDialog = false
                    viewModel.stopAndSaveRecording(titleText)
                }) {
                    Text("Save", color = Accent)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSaveDialog = false
                    viewModel.stopAndSaveRecording("")
                }) {
                    Text("Skip", color = TextSecondary)
                }
            }
        )
    }
}

// ── Sub-components ─────────────────────────────────────────────────────────────

@Composable
fun HudMetric(label: String, value: String, align: Alignment.Horizontal = Alignment.Start) {
    Column(horizontalAlignment = align) {
        Text(label, fontFamily = FontFamily.Monospace, fontSize = 7.sp, letterSpacing = 1.5.sp, color = TextSecondary.copy(0.5f))
        Text(value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextPrimary.copy(0.85f))
    }
}

@Composable
fun LiveStatusPill(isListening: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Surface700.copy(alpha = 0.8f))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isListening) Accent else TextSecondary)
        )
        Text(
            if (isListening) "LIVE" else "PAUSED",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            letterSpacing = 1.5.sp,
            color = if (isListening) Accent else TextSecondary
        )
    }
}

@Composable
fun RecordButton(isRecording: Boolean, onStart: () -> Unit, onStop: () -> Unit) {
    val bgColor = if (isRecording) AccentRed else Surface600
    val icon    = if (isRecording) Icons.Rounded.Stop else Icons.Rounded.FiberManualRecord

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = if (isRecording) onStop else onStart) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
fun RecordingBadge(ms: Long) {
    val secs  = ms / 1000
    val label = "%d:%02d".format(secs / 60, secs % 60)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(AccentRed.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(AccentRed))
        Text("REC  $label", fontFamily = FontFamily.Monospace, fontSize = 10.sp, letterSpacing = 1.sp, color = AccentRed)
    }
}

private fun formatFreqHud(hz: Float): String =
    if (hz >= 1000f) "${"%.1f".format(hz / 1000f)}k" else "${hz.toInt()}"