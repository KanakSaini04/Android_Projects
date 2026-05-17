package com.lensora.presentation.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.lensora.core.ui.theme.*
import com.lensora.domain.model.*
import com.lensora.domain.usecase.camera.*
import com.lensora.presentation.camera.components.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraUiState(
    val isFlashOn: Boolean = false,
    val isHdrOn: Boolean = true,
    val currentMode: CameraMode = CameraMode.AUTO,
    val currentFilter: CinematicFilter = CinematicFilter.CINEMATIC,
    val detectedScene: String? = null,
    val compositionHint: String? = null,
    val poseResult: PoseResult? = null,
    val isPoseOverlayEnabled: Boolean = false,
    val isCompositionGuideEnabled: Boolean = true,
    val lastPhotoUri: Uri? = null,
    val error: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val detectSceneUseCase: DetectSceneUseCase,
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val analyzePoseUseCase: AnalyzePoseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState = _uiState.asStateFlow()

    fun onFrameAnalyzed(imageProxy: androidx.camera.core.ImageProxy) {
        viewModelScope.launch {
            val scene = detectSceneUseCase(imageProxy)
            val filter = mapSceneToFilter(scene)
            val hint = generateCompositionHint(scene)
            _uiState.update { it.copy(detectedScene = scene, currentFilter = filter, compositionHint = hint) }
            if (_uiState.value.isPoseOverlayEnabled) {
                val pose = analyzePoseUseCase(imageProxy)
                _uiState.update { it.copy(poseResult = pose) }
            }
            imageProxy.close()
        }
    }

    fun capturePhoto(context: Context) {
        viewModelScope.launch {
            capturePhotoUseCase(context)
                .onSuccess { uri -> _uiState.update { it.copy(lastPhotoUri = uri) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun toggleFlash() = _uiState.update { it.copy(isFlashOn = !it.isFlashOn) }
    fun toggleHdr() = _uiState.update { it.copy(isHdrOn = !it.isHdrOn) }
    fun togglePoseOverlay() = _uiState.update { it.copy(isPoseOverlayEnabled = !it.isPoseOverlayEnabled) }
    fun toggleCompositionGuide() = _uiState.update { it.copy(isCompositionGuideEnabled = !it.isCompositionGuideEnabled) }
    fun flipCamera() {}
    fun setMode(mode: CameraMode) = _uiState.update { it.copy(currentMode = mode) }

    private fun mapSceneToFilter(scene: String?): CinematicFilter = when (scene?.lowercase()) {
        "beach", "sunset", "outdoor" -> CinematicFilter.GOLDEN_HOUR
        "night", "dark" -> CinematicFilter.TOKYO_NIGHT
        "portrait", "person" -> CinematicFilter.SOFT_PORTRAIT
        "mountain", "nature" -> CinematicFilter.MOUNTAIN_AIR
        "street", "city" -> CinematicFilter.MOODY_STREET
        else -> CinematicFilter.CINEMATIC
    }

    private fun generateCompositionHint(scene: String?): String? = when (scene?.lowercase()) {
        "portrait" -> "Move slightly left for better framing"
        "sunset" -> "Tilt phone lower to capture horizon"
        "street" -> "Try leading lines for depth"
        else -> null
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permissions = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))

    LaunchedEffect(Unit) { permissions.launchMultiplePermissionRequest() }

    if (!permissions.allPermissionsGranted) {
        Box(modifier = Modifier.fillMaxSize().background(Black), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                Text("📷", fontSize = 52.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Camera Permission Required", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Lensora needs camera access to work.", color = WhiteDim, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { permissions.launchMultiplePermissionRequest() }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) {
                    Text("Grant Permission", color = Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Black)) {
        CameraPreview(modifier = Modifier.fillMaxSize(), currentFilter = uiState.currentFilter, onFrameAnalyzed = { viewModel.onFrameAnalyzed(it) })

        if (uiState.isPoseOverlayEnabled) PoseOverlay(modifier = Modifier.fillMaxSize(), poseResult = uiState.poseResult)
        if (uiState.isCompositionGuideEnabled) CompositionGuide(modifier = Modifier.fillMaxSize())

        CameraTopBar(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            isFlashOn = uiState.isFlashOn,
            isHdrOn = uiState.isHdrOn,
            onFlashToggle = { viewModel.toggleFlash() },
            onHdrToggle = { viewModel.toggleHdr() },
            onSettingsClick = { navController.navigate("settings") }
        )

        AnimatedVisibility(visible = uiState.detectedScene != null, modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp), enter = fadeIn() + slideInVertically(), exit = fadeOut()) {
            Box(modifier = Modifier.background(ElectricBlueDim.copy(alpha = 0.8f), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text("🎬 ${uiState.detectedScene ?: ""}", color = White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        AnimatedVisibility(visible = uiState.compositionHint != null, modifier = Modifier.align(Alignment.Center).padding(top = 120.dp), enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.background(Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(uiState.compositionHint ?: "", color = ElectricBlue, fontSize = 13.sp)
            }
        }

        CameraBottomBar(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp),
            onCapture = { viewModel.capturePhoto(context) },
            onFlipCamera = { viewModel.flipCamera() },
            onGalleryClick = { navController.navigate("gallery") },
            currentMode = uiState.currentMode,
            onModeChange = { viewModel.setMode(it) },
            lastPhotoUri = uiState.lastPhotoUri,
            isPoseOverlayEnabled = uiState.isPoseOverlayEnabled,
            isCompositionGuideEnabled = uiState.isCompositionGuideEnabled,
            onPoseToggle = { viewModel.togglePoseOverlay() },
            onCompositionToggle = { viewModel.toggleCompositionGuide() }
        )
    }
}
