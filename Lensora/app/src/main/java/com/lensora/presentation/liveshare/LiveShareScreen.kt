package com.lensora.presentation.liveshare

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lensora.core.ui.theme.*
import com.lensora.domain.usecase.liveshare.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveShareUiState(
    val isLoading: Boolean = false,
    val isSessionActive: Boolean = false,
    val isViewingSession: Boolean = false,
    val sessionCode: String? = null,
    val qrBitmap: Bitmap? = null,
    val viewerCount: Int = 0,
    val sessionDurationSeconds: Int = 0,
    val error: String? = null
)

@HiltViewModel
class LiveShareViewModel @Inject constructor(
    private val startSessionUseCase: StartLiveShareSessionUseCase,
    private val stopSessionUseCase: StopLiveShareSessionUseCase,
    private val joinSessionUseCase: JoinLiveShareSessionUseCase,
    private val generateQrUseCase: GenerateQrCodeUseCase,
    private val observeViewersUseCase: ObserveViewersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveShareUiState())
    val uiState = _uiState.asStateFlow()
    private var timerJob: Job? = null
    private var viewerJob: Job? = null

    fun startSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            startSessionUseCase()
                .onSuccess { code ->
                    val qrBitmap = generateQrUseCase(code)
                    _uiState.update { it.copy(isLoading = false, isSessionActive = true, sessionCode = code, qrBitmap = qrBitmap) }
                    startTimer(); observeViewers(code)
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun stopSession() {
        viewModelScope.launch {
            stopSessionUseCase()
            timerJob?.cancel(); viewerJob?.cancel()
            _uiState.update { it.copy(isSessionActive = false, sessionCode = null, qrBitmap = null, viewerCount = 0, sessionDurationSeconds = 0) }
        }
    }

    fun joinWithCode(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            joinSessionUseCase(code)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isViewingSession = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch { while (true) { delay(1000); _uiState.update { it.copy(sessionDurationSeconds = it.sessionDurationSeconds + 1) } } }
    }

    private fun observeViewers(code: String) {
        viewerJob = viewModelScope.launch { observeViewersUseCase(code).collect { count -> _uiState.update { it.copy(viewerCount = count) } } }
    }

    override fun onCleared() { super.onCleared(); timerJob?.cancel(); viewerJob?.cancel() }
}

@Composable
fun LiveShareScreen(navController: NavController, viewModel: LiveShareViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(Black)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = White) }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Live Share", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
                    Text("Share camera view in real time", fontSize = 12.sp, color = WhiteDim)
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).background(SurfaceGray, RoundedCornerShape(12.dp)).padding(4.dp)) {
                TabBtn("Share My Camera", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                TabBtn("Join Session", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> HostContent(uiState = uiState, onStart = { viewModel.startSession() }, onStop = { viewModel.stopSession() })
                1 -> JoinContent(uiState = uiState, onJoin = { viewModel.joinWithCode(it) })
            }
        }
    }
}

@Composable
private fun TabBtn(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = if (selected) ElectricBlue else Color.Transparent, contentColor = if (selected) Black else WhiteDim), elevation = ButtonDefaults.buttonElevation(0.dp)) {
        Text(text, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
    }
}

@Composable
private fun HostContent(uiState: LiveShareUiState, onStart: () -> Unit, onStop: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (!uiState.isSessionActive) {
            InfoBox("📡", "How it works", "Generate a QR code. Friends scan it to see your camera view and pose overlay in real time.")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue), enabled = !uiState.isLoading) {
                if (uiState.isLoading) CircularProgressIndicator(color = Black, modifier = Modifier.size(22.dp))
                else { Icon(Icons.Default.QrCode, contentDescription = null, tint = Black); Spacer(modifier = Modifier.width(8.dp)); Text("Generate QR Code", color = Black, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color(0xFF1B4D1B).copy(alpha = 0.8f), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 6.dp)) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Session Active • ${uiState.viewerCount} viewer${if (uiState.viewerCount != 1) "s" else ""}", color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(24.dp))
            uiState.qrBitmap?.let { bitmap ->
                Box(modifier = Modifier.size(220.dp).background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(16.dp)).padding(16.dp)) {
                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.fillMaxSize())
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            uiState.sessionCode?.let { code ->
                Text("Session Code", color = WhiteDim, fontSize = 12.sp)
                Text(code, color = ElectricBlue, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 6.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Share this code or QR with friends", color = WhiteDim, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Duration: ${formatDuration(uiState.sessionDurationSeconds)}", color = WhiteDim, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = onStop, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error)))) {
                Icon(Icons.Default.Stop, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Stop Session", fontWeight = FontWeight.Bold)
            }
        }
        AnimatedVisibility(visible = uiState.error != null) { Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp)) }
    }
}

@Composable
private fun JoinContent(uiState: LiveShareUiState, onJoin: (String) -> Unit) {
    var code by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (uiState.isViewingSession) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color(0xFF1B4D1B).copy(alpha = 0.8f), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 6.dp)) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connected • Live", color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(9f / 16f).clip(RoundedCornerShape(16.dp)).background(SurfaceGray).border(1.dp, ElectricBlueDim, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📡", fontSize = 36.sp); Spacer(modifier = Modifier.height(8.dp))
                    Text("Live Camera Feed", color = WhiteDim, fontSize = 14.sp); Text("with Pose Overlay", color = ElectricBlue, fontSize = 13.sp)
                }
            }
        } else {
            InfoBox("👁️", "Join a session", "Scan a QR code or enter a session code to view someone's live camera feed.")
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceGray)
                Text("  enter code  ", color = WhiteDim, fontSize = 13.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceGray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = code, onValueChange = { if (it.length <= 6) code = it.uppercase() }, placeholder = { Text("XXXXXX", color = WhiteDim) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue, unfocusedBorderColor = SurfaceGray, focusedTextColor = ElectricBlue, unfocusedTextColor = ElectricBlue, cursorColor = ElectricBlue, focusedContainerColor = SurfaceGray, unfocusedContainerColor = SurfaceGray), singleLine = true)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { if (code.length == 6) onJoin(code) }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = if (code.length == 6) ElectricBlueDim else SurfaceGray), enabled = code.length == 6 && !uiState.isLoading) {
                if (uiState.isLoading) CircularProgressIndicator(color = White, modifier = Modifier.size(22.dp))
                else Text("Join Session", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            AnimatedVisibility(visible = uiState.error != null) { Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp)) }
        }
    }
}

@Composable
private fun InfoBox(icon: String, title: String, description: String) {
    Box(modifier = Modifier.fillMaxWidth().background(SurfaceGray, RoundedCornerShape(16.dp)).padding(20.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 36.sp); Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(8.dp))
            Text(description, color = WhiteDim, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

private fun formatDuration(seconds: Int): String = "%02d:%02d".format(seconds / 60, seconds % 60)
