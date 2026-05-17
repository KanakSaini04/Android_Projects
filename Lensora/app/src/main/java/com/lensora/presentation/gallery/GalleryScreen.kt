package com.lensora.presentation.gallery

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.lensora.domain.model.GalleryPhoto
import com.lensora.domain.usecase.gallery.*
import com.lensora.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GalleryUiState(
    val isLoading: Boolean = false,
    val photos: List<GalleryPhoto> = emptyList(),
    val bestShots: List<GalleryPhoto> = emptyList(),
    val travelPhotos: List<GalleryPhoto> = emptyList(),
    val portraits: List<GalleryPhoto> = emptyList(),
    val highlights: List<String> = emptyList(),
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<String> = emptySet()
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val getBestShotsUseCase: GetBestShotsUseCase,
    private val deletePhotosUseCase: DeletePhotosUseCase,
    private val getHighlightsUseCase: GetHighlightsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState = _uiState.asStateFlow()

    init { loadPhotos() }

    fun loadPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val photos = getPhotosUseCase()
            val bestShots = getBestShotsUseCase(photos)
            val highlights = getHighlightsUseCase(photos)
            _uiState.update { it.copy(isLoading = false, photos = photos, bestShots = bestShots, travelPhotos = photos.filter { p -> p.tags.contains("travel") }, portraits = photos.filter { p -> p.tags.contains("portrait") }, highlights = highlights) }
        }
    }

    fun toggleSelectionMode() = _uiState.update { it.copy(isSelectionMode = !it.isSelectionMode, selectedIds = emptySet()) }
    fun toggleSelection(id: String) = _uiState.update { val newIds = if (it.selectedIds.contains(id)) it.selectedIds - id else it.selectedIds + id; it.copy(selectedIds = newIds) }
    fun deleteSelected() { viewModelScope.launch { deletePhotosUseCase(_uiState.value.selectedIds.toList()); loadPhotos(); _uiState.update { it.copy(isSelectionMode = false, selectedIds = emptySet()) } } }
}

@Composable
fun GalleryScreen(navController: NavController, viewModel: GalleryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Best Shots", "Travel", "Portraits")

    Column(modifier = Modifier.fillMaxSize().background(Black).statusBarsPadding()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Gallery", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
            IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WhiteDim)
            }
        }

        if (uiState.highlights.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).background(SurfaceGray, RoundedCornerShape(14.dp)).padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("AI Highlights", color = ElectricBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(uiState.highlights.firstOrNull() ?: "", color = WhiteDim, fontSize = 12.sp)
                    }
                }
            }
        }

        ScrollableTabRow(selectedTabIndex = selectedTab, containerColor = Black, contentColor = ElectricBlue, edgePadding = 16.dp, divider = {}) {
            tabs.forEachIndexed { index, tab ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(tab, color = if (selectedTab == index) ElectricBlue else WhiteDim, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp) })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ElectricBlue) }
        } else if (uiState.photos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷", fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No photos yet", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Start capturing cinematic moments", color = WhiteDim, fontSize = 14.sp)
                }
            }
        } else {
            val displayPhotos = when (selectedTab) { 1 -> uiState.bestShots; 2 -> uiState.travelPhotos; 3 -> uiState.portraits; else -> uiState.photos }
            LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(2.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.fillMaxSize()) {
                items(displayPhotos, key = { it.id }) { photo ->
                    Box(modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(4.dp)).clickable {
                        if (uiState.isSelectionMode) viewModel.toggleSelection(photo.id)
                        else navController.navigate(Screen.Editor.createRoute(photo.uri.toString()))
                    }) {
                        AsyncImage(model = photo.uri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        if (photo.isBestShot) Box(modifier = Modifier.align(Alignment.TopStart).padding(4.dp).background(ElectricBlue.copy(alpha = 0.9f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) { Text("★", color = Black, fontSize = 10.sp) }
                        
                        if (uiState.isSelectionMode) {
                            Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(22.dp).background(if (uiState.selectedIds.contains(photo.id)) ElectricBlue else Black.copy(alpha = 0.5f), RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
                                if (uiState.selectedIds.contains(photo.id)) Icon(Icons.Default.Check, contentDescription = null, tint = Black, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
