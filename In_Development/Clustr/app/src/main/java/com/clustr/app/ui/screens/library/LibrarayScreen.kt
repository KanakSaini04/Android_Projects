package com.clustr.app.ui.screens.library

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.clustr.app.VoiceRecord
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clustr.app.toVoiceNode
import com.clustr.app.ui.screens.visualizer.ClustrCanvas
import com.clustr.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LibraryScreen(uid: String) {
    val vm: LibraryViewModel = viewModel()
    val state by vm.state.collectAsState()
    var selectedRecord by remember { mutableStateOf<VoiceRecord?>(null) }

    LaunchedEffect(uid) { vm.loadRecordings(uid) }

    // Detail overlay (snapshot view + playback info)
    AnimatedVisibility(
        visible = selectedRecord != null,
        enter = fadeIn() + slideInVertically { it },
        exit  = fadeOut() + slideOutVertically { it }
    ) {
        selectedRecord?.let { rec ->
            RecordDetailSheet(
                record  = rec,
                onClose = { selectedRecord = null },
                onDelete = { vm.deleteRecording(rec); selectedRecord = null }
            )
        }
    }

    if (selectedRecord == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Library", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${state.recordings.size} recordings",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
                Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = Accent, modifier = Modifier.size(22.dp))
            }

            HorizontalDivider(color = Divider, thickness = 0.5.dp)

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
            } else if (state.recordings.isEmpty()) {
                EmptyLibrary()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.recordings, key = { it.id }) { record ->
                        RecordingCard(
                            record  = record,
                            onClick = { selectedRecord = record }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecordingCard(record: VoiceRecord, onClick: () -> Unit) {
    val sdf = remember { SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault()) }
    val dateStr = record.createdAt?.toDate()?.let { sdf.format(it) } ?: ""
    val durationStr = formatDuration(record.durationMs)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface800)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Waveform icon placeholder
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentDim),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.GraphicEq,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                record.title.ifBlank { "Untitled Recording" },
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
            Spacer(Modifier.height(3.dp))
            Text(
                dateStr,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                durationStr,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = Accent
            )
            Spacer(Modifier.height(3.dp))
            Text(
                "${record.nodeSnapshots.size} nodes",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun RecordDetailSheet(
    record: VoiceRecord,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    val frozenNodes = remember(record) { record.nodeSnapshots.map { it.toVoiceNode() } }
    var rotation by remember { mutableStateOf(0f) }

    // Slowly auto-rotate the frozen snapshot
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(16)
            rotation += 0.004f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        ClustrCanvas(nodes = frozenNodes, rotationY = rotation, modifier = Modifier.fillMaxSize())

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Rounded.Close, contentDescription = "Close", tint = TextPrimary)
            }
            Text(
                record.title.ifBlank { "Recording" },
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", tint = AccentRed)
            }
        }

        // Bottom info card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Surface800.copy(alpha = 0.92f))
                .padding(20.dp)
        ) {
            Text("RECORDING DATA", fontFamily = FontFamily.Monospace, fontSize = 9.sp, letterSpacing = 2.sp, color = TextSecondary)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoChip("Duration", formatDuration(record.durationMs))
                InfoChip("Nodes", "${record.nodeSnapshots.size}")
                InfoChip("Peak", "${formatPeakHz(record.peakFrequencyHz)}Hz")
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontFamily = FontFamily.Monospace, fontSize = 9.sp, letterSpacing = 1.sp, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
    }
}

@Composable
fun EmptyLibrary() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = Surface500, modifier = Modifier.size(52.dp))
            Text("No recordings yet", style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary))
            Text("Hit record on the Visualizer tab\nto capture an acoustic pattern", style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary, fontSize = 13.sp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

private fun formatDuration(ms: Long): String {
    val secs = ms / 1000; return "%d:%02d".format(secs / 60, secs % 60)
}
private fun formatPeakHz(hz: Float): String =
    if (hz >= 1000f) "${"%.1f".format(hz / 1000f)}k" else "${hz.toInt()}"
