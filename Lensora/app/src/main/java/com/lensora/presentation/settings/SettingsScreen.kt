package com.lensora.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lensora.core.ui.theme.*
import com.lensora.domain.usecase.settings.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val autoSceneDetection: Boolean = true,
    val hdrDefault: Boolean = true,
    val autoCapture: Boolean = false,
    val hapticFeedback: Boolean = true,
    val poseGuidance: Boolean = false,
    val compositionGuide: Boolean = true,
    val dslrBlur: Boolean = true,
    val saveOriginal: Boolean = false,
    val photoQuality: String = "High"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init { viewModelScope.launch { _uiState.value = getSettingsUseCase() } }

    private fun save() { viewModelScope.launch { saveSettingsUseCase(_uiState.value) } }

    fun toggleAutoSceneDetection() { _uiState.update { it.copy(autoSceneDetection = !it.autoSceneDetection) }; save() }
    fun toggleHdrDefault() { _uiState.update { it.copy(hdrDefault = !it.hdrDefault) }; save() }
    fun toggleAutoCapture() { _uiState.update { it.copy(autoCapture = !it.autoCapture) }; save() }
    fun toggleHapticFeedback() { _uiState.update { it.copy(hapticFeedback = !it.hapticFeedback) }; save() }
    fun togglePoseGuidance() { _uiState.update { it.copy(poseGuidance = !it.poseGuidance) }; save() }
    fun toggleCompositionGuide() { _uiState.update { it.copy(compositionGuide = !it.compositionGuide) }; save() }
    fun toggleDslrBlur() { _uiState.update { it.copy(dslrBlur = !it.dslrBlur) }; save() }
    fun toggleSaveOriginal() { _uiState.update { it.copy(saveOriginal = !it.saveOriginal) }; save() }
    fun setPhotoQuality(quality: String) { _uiState.update { it.copy(photoQuality = quality) }; save() }
}

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Black).statusBarsPadding().verticalScroll(rememberScrollState())) {
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))

        SectionLabel("Camera")
        SettingToggle(Icons.Default.AutoAwesome, "Auto Scene Detection", "Automatically detect and apply best settings", uiState.autoSceneDetection) { viewModel.toggleAutoSceneDetection() }
        SettingToggle(Icons.Default.HdrOn, "HDR by Default", "Enable HDR for every shot", uiState.hdrDefault) { viewModel.toggleHdrDefault() }
        SettingToggle(Icons.Default.CenterFocusStrong, "Auto Capture", "Capture when pose and framing are perfect", uiState.autoCapture) { viewModel.toggleAutoCapture() }
        SettingToggle(Icons.Default.Vibration, "Haptic Feedback", "Vibrate on capture", uiState.hapticFeedback) { viewModel.toggleHapticFeedback() }

        SectionLabel("AI Features")
        SettingToggle(Icons.Default.AccessibilityNew, "Pose Guidance", "Show live pose overlay", uiState.poseGuidance) { viewModel.togglePoseGuidance() }
        SettingToggle(Icons.Default.GridOn, "Composition Guide", "Show rule of thirds grid", uiState.compositionGuide) { viewModel.toggleCompositionGuide() }
        SettingToggle(Icons.Default.BlurOn, "DSLR Blur", "Apply cinematic background blur", uiState.dslrBlur) { viewModel.toggleDslrBlur() }

        SectionLabel("Storage")
        SettingToggle(Icons.Default.HighQuality, "Save Original", "Keep original photo alongside enhanced", uiState.saveOriginal) { viewModel.toggleSaveOriginal() }

        SectionLabel("About")
        SettingInfo(Icons.Default.Info, "Version", "1.0.0")
        SettingInfo(Icons.Default.Movie, "App", "Lensora AI")

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(title, fontSize = 13.sp, color = ElectricBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
}

@Composable
private fun SettingToggle(icon: ImageVector, label: String, description: String, checked: Boolean, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onToggle() }.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = White, fontSize = 15.sp)
            Text(description, color = WhiteDim, fontSize = 12.sp)
        }
        Switch(checked = checked, onCheckedChange = { onToggle() }, colors = SwitchDefaults.colors(checkedThumbColor = Black, checkedTrackColor = ElectricBlue, uncheckedThumbColor = WhiteDim, uncheckedTrackColor = SurfaceGray))
    }
}

@Composable
private fun SettingInfo(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, color = White, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Text(value, color = WhiteDim, fontSize = 14.sp)
    }
}
