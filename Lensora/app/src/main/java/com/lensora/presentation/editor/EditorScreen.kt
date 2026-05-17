package com.lensora.presentation.editor

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.lensora.core.ui.theme.*
import com.lensora.domain.model.CinematicFilter
import com.lensora.domain.model.EditorTab
import com.lensora.domain.usecase.editor.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val originalUri: Uri? = null,
    val editedUri: Uri? = null,
    val selectedTab: EditorTab = EditorTab.AI_ENHANCE,
    val selectedFilter: CinematicFilter? = null,
    val sliders: Map<String, Float> = emptyMap(),
    val isEnhancing: Boolean = false,
    val isComparing: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val aiEnhanceUseCase: AiEnhanceUseCase,
    private val applyFilterUseCase: ApplyFilterUseCase,
    private val savePhotoUseCase: SavePhotoUseCase,
    private val sharePhotoUseCase: SharePhotoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = _uiState.asStateFlow()

    fun loadPhoto(uri: Uri) = _uiState.update { it.copy(originalUri = uri, editedUri = uri) }

    fun aiEnhance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isEnhancing = true) }
            val uri = _uiState.value.originalUri ?: return@launch
            aiEnhanceUseCase(uri)
                .onSuccess { enhanced -> _uiState.update { it.copy(editedUri = enhanced, isEnhancing = false) } }
                .onFailure { _uiState.update { it.copy(isEnhancing = false) } }
        }
    }

    fun applyFilter(filter: CinematicFilter) {
        viewModelScope.launch {
            val uri = _uiState.value.originalUri ?: return@launch
            _uiState.update { it.copy(selectedFilter = filter) }
            applyFilterUseCase(uri, filter).onSuccess { filtered -> _uiState.update { it.copy(editedUri = filtered) } }
        }
    }

    fun adjustSlider(key: String, value: Float) {
        val newSliders = _uiState.value.sliders.toMutableMap()
        newSliders[key] = value
        _uiState.update { it.copy(sliders = newSliders) }
    }

    fun savePhoto() { viewModelScope.launch { val uri = _uiState.value.editedUri ?: return@launch; savePhotoUseCase(uri).onSuccess { _uiState.update { it.copy(isSaved = true) } } } }
    fun sharePhoto() { viewModelScope.launch { val uri = _uiState.value.editedUri ?: return@launch; sharePhotoUseCase(uri) } }
    fun selectTab(tab: EditorTab) = _uiState.update { it.copy(selectedTab = tab) }
    fun toggleCompare() = _uiState.update { it.copy(isComparing = !it.isComparing) }
    fun applyCrop(crop: String) {}
}

@Composable
fun EditorScreen(navController: NavController, photoUri: String, viewModel: EditorViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val uri = Uri.parse(photoUri)
    LaunchedEffect(uri) { viewModel.loadPhoto(uri) }

    Box(modifier = Modifier.fillMaxSize().background(Black)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = White) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.background(if (uiState.isComparing) ElectricBlueDim.copy(alpha = 0.5f) else SurfaceGray, RoundedCornerShape(8.dp)).clickable { viewModel.toggleCompare() }.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Compare", color = if (uiState.isComparing) ElectricBlue else WhiteDim, fontSize = 13.sp)
                    }
                    IconButton(onClick = { viewModel.sharePhoto() }) { Icon(Icons.Default.Share, contentDescription = null, tint = WhiteDim) }
                    Button(onClick = { viewModel.savePhoto() }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                        Text("Save", color = Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // Preview
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                AsyncImage(model = uiState.editedUri ?: uri, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                if (uiState.isComparing) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(model = uri, contentDescription = "Original", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxHeight().weight(1f))
                        AsyncImage(model = uiState.editedUri ?: uri, contentDescription = "Enhanced", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxHeight().weight(1f))
                    }
                    Box(modifier = Modifier.fillMaxHeight().width(2.dp).background(ElectricBlue).align(Alignment.Center))
                    Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text("ORIGINAL", color = WhiteDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("ENHANCED", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Editor Panel
            Column(modifier = Modifier.fillMaxWidth().background(DarkGray).navigationBarsPadding()) {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EditorTab.values()) { tab ->
                        Box(modifier = Modifier.background(if (uiState.selectedTab == tab) ElectricBlue else SurfaceGray, RoundedCornerShape(20.dp)).clickable { viewModel.selectTab(tab) }.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text(tab.label, color = if (uiState.selectedTab == tab) Black else WhiteDim, fontSize = 13.sp, fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp)) {
                    when (uiState.selectedTab) {
                        EditorTab.AI_ENHANCE -> Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            if (uiState.isEnhancing) { CircularProgressIndicator(color = ElectricBlue); Spacer(modifier = Modifier.height(12.dp)); Text("Enhancing...", color = WhiteDim, fontSize = 14.sp) }
                            else { Button(onClick = { viewModel.aiEnhance() }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Black); Spacer(modifier = Modifier.width(8.dp)); Text("One-Tap AI Enhance", color = Black, fontWeight = FontWeight.Bold, fontSize = 15.sp) } }
                        }
                        EditorTab.CINEMATIC -> LazyRow(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            items(CinematicFilter.values()) { filter ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { viewModel.applyFilter(filter) }) {
                                    Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(colors = filterColors(filter))))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(filter.name.replace("_", " "), color = if (uiState.selectedFilter == filter) ElectricBlue else WhiteDim, fontSize = 10.sp)
                                }
                            }
                        }
                        EditorTab.LIGHT -> SliderPanelContent(sliders = listOf("Exposure" to (uiState.sliders["exposure"] ?: 0f), "Highlights" to (uiState.sliders["highlights"] ?: 0f), "Shadows" to (uiState.sliders["shadows"] ?: 0f)), onSliderChange = { k, v -> viewModel.adjustSlider(k, v) })
                        EditorTab.COLOR -> SliderPanelContent(sliders = listOf("Warmth" to (uiState.sliders["warmth"] ?: 0f), "Vibrance" to (uiState.sliders["vibrance"] ?: 0f), "Saturation" to (uiState.sliders["saturation"] ?: 0f)), onSliderChange = { k, v -> viewModel.adjustSlider(k, v) })
                        EditorTab.DETAILS -> SliderPanelContent(sliders = listOf("Sharpen" to (uiState.sliders["sharpen"] ?: 0f), "Clarity" to (uiState.sliders["clarity"] ?: 0f), "Denoise" to (uiState.sliders["denoise"] ?: 0f)), onSliderChange = { k, v -> viewModel.adjustSlider(k, v) })
                        EditorTab.PORTRAIT -> SliderPanelContent(sliders = listOf("Skin Tone" to (uiState.sliders["skin"] ?: 0f), "Eye Enhance" to (uiState.sliders["eye"] ?: 0f)), onSliderChange = { k, v -> viewModel.adjustSlider(k, v) })
                        EditorTab.BLUR -> SliderPanelContent(sliders = listOf("Blur Intensity" to (uiState.sliders["blur"] ?: 0f)), onSliderChange = { k, v -> viewModel.adjustSlider(k, v) })
                        EditorTab.CROP -> LazyRow(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            items(listOf("Original", "Instagram", "Cinematic", "Wallpaper", "YouTube")) { crop ->
                                Box(modifier = Modifier.background(SurfaceGray, RoundedCornerShape(12.dp)).clickable { viewModel.applyCrop(crop) }.padding(horizontal = 16.dp, vertical = 12.dp)) { Text(crop, color = White, fontSize = 13.sp) }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (uiState.isSaved) {
            Box(modifier = Modifier.align(Alignment.Center).background(SurfaceGray.copy(alpha = 0.9f), RoundedCornerShape(16.dp)).padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saved to Gallery", color = White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SliderPanelContent(sliders: List<Pair<String, Float>>, onSliderChange: (String, Float) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        sliders.forEach { (label, value) ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(label, color = WhiteDim, fontSize = 12.sp, modifier = Modifier.width(90.dp))
                Slider(value = value, onValueChange = { onSliderChange(label.lowercase(), it) }, valueRange = -1f..1f, modifier = Modifier.weight(1f), colors = SliderDefaults.colors(thumbColor = ElectricBlue, activeTrackColor = ElectricBlue, inactiveTrackColor = SurfaceGray))
                Text("${(value * 100).toInt()}", color = ElectricBlue, fontSize = 11.sp, modifier = Modifier.width(32.dp))
            }
        }
    }
}

private fun filterColors(filter: CinematicFilter): List<Color> = when (filter) {
    CinematicFilter.GOLDEN_HOUR -> listOf(Color(0xFFFF8C00), Color(0xFFFFD700))
    CinematicFilter.TOKYO_NIGHT -> listOf(Color(0xFF0D0221), Color(0xFF4FC3F7))
    CinematicFilter.SOFT_PORTRAIT -> listOf(Color(0xFFFFE4E1), Color(0xFFDEB887))
    CinematicFilter.MOUNTAIN_AIR -> listOf(Color(0xFF87CEEB), Color(0xFF228B22))
    CinematicFilter.MOODY_STREET -> listOf(Color(0xFF2F2F2F), Color(0xFF4A4A4A))
    CinematicFilter.TROPICAL_WARM -> listOf(Color(0xFF00CED1), Color(0xFFFF6347))
    CinematicFilter.VINTAGE_FILM -> listOf(Color(0xFFD2B48C), Color(0xFF8B4513))
    else -> listOf(Color(0xFF1A1A2E), Color(0xFF4FC3F7))
}
