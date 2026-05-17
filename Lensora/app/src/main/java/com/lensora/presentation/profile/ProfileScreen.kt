package com.lensora.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.lensora.domain.model.UserProfile
import com.lensora.domain.usecase.profile.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "", val email: String = "", val photoUrl: String? = null,
    val isGuest: Boolean = false, val guestDaysLeft: Int = 7,
    val totalPhotos: Int = 0, val bestShots: Int = 0,
    val savedPresets: Int = 0, val presets: List<String> = emptyList(),
    val showEditDialog: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetFromProfileUseCase,
    private val getPresetsUseCase: GetPresetsUseCase,
    private val deletePresetUseCase: DeletePresetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            val profile = getProfileUseCase()
            val presets = getPresetsUseCase()
            _uiState.update { it.copy(name = profile.name, email = profile.email, photoUrl = profile.photoUrl, isGuest = profile.isGuest, guestDaysLeft = profile.guestDaysLeft, totalPhotos = profile.totalPhotos, bestShots = profile.bestShots, savedPresets = presets.size, presets = presets) }
        }
    }

    fun updateProfile(name: String) { viewModelScope.launch { updateProfileUseCase(name); _uiState.update { it.copy(name = name, showEditDialog = false) } } }
    fun signOut() { viewModelScope.launch { signOutUseCase() } }
    fun sendPasswordReset() { viewModelScope.launch { sendPasswordResetUseCase(_uiState.value.email) } }
    fun deletePreset(name: String) { viewModelScope.launch { deletePresetUseCase(name); loadProfile() } }
    fun showEditDialog() = _uiState.update { it.copy(showEditDialog = true) }
    fun hideEditDialog() = _uiState.update { it.copy(showEditDialog = false) }
}

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Black).statusBarsPadding().verticalScroll(rememberScrollState())) {
        // Header
        Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(colors = listOf(ElectricBlueDim.copy(alpha = 0.15f), Color.Transparent))).padding(24.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier.size(90.dp).border(2.dp, ElectricBlue, CircleShape).padding(3.dp).clip(CircleShape).background(SurfaceGray), contentAlignment = Alignment.Center) {
                        if (uiState.photoUrl != null) AsyncImage(model = uiState.photoUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        else Text(uiState.name.firstOrNull()?.toString() ?: "G", fontSize = 32.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.size(26.dp).background(ElectricBlue, CircleShape).clickable { viewModel.showEditDialog() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Black, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(if (uiState.isGuest) "Guest User" else uiState.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
                if (!uiState.isGuest) Text(uiState.email, fontSize = 13.sp, color = WhiteDim)
            }
        }

        if (uiState.isGuest) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).background(ElectricBlueDim.copy(alpha = 0.2f), RoundedCornerShape(14.dp)).border(1.dp, ElectricBlueDim, RoundedCornerShape(14.dp)).padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("Guest Trial", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp); Text("${uiState.guestDaysLeft} days remaining", color = WhiteDim, fontSize = 12.sp) }
                Button(onClick = { navController.navigate("auth") }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("Sign Up", color = Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Stats
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(SurfaceGray, RoundedCornerShape(14.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(uiState.totalPhotos.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ElectricBlue); Text("Photos", fontSize = 11.sp, color = WhiteDim) }
            VerticalDivider(modifier = Modifier.height(40.dp), color = DarkGray)
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(uiState.bestShots.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ElectricBlue); Text("Best Shots", fontSize = 11.sp, color = WhiteDim) }
            VerticalDivider(modifier = Modifier.height(40.dp), color = DarkGray)
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(uiState.savedPresets.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ElectricBlue); Text("Presets", fontSize = 11.sp, color = WhiteDim) }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        ProfileActionRow(Icons.Default.Edit, "Edit Profile") { viewModel.showEditDialog() }
        ProfileActionRow(Icons.Default.Lock, "Change Password") { viewModel.sendPasswordReset() }
        ProfileActionRow(Icons.Default.Logout, "Sign Out", tint = MaterialTheme.colorScheme.error) {
            viewModel.signOut()
            navController.navigate("auth") { popUpTo(0) { inclusive = true } }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (uiState.showEditDialog) {
        var name by remember { mutableStateOf(uiState.name) }
        AlertDialog(
            onDismissRequest = { viewModel.hideEditDialog() },
            containerColor = SurfaceGray,
            title = { Text("Edit Profile", color = White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name", color = WhiteDim) }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue, unfocusedBorderColor = DarkGray, focusedTextColor = White, unfocusedTextColor = White, cursorColor = ElectricBlue), modifier = Modifier.fillMaxWidth())
            },
            confirmButton = { Button(onClick = { viewModel.updateProfile(name) }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) { Text("Save", color = Black, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { viewModel.hideEditDialog() }) { Text("Cancel", color = WhiteDim) } }
        )
    }
}

@Composable
private fun ProfileActionRow(icon: ImageVector, label: String, tint: Color = White, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, color = tint, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = WhiteDim, modifier = Modifier.size(18.dp))
    }
}
