package com.codexcraft.caretap.ui.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.codexcraft.caretap.data.model.Profile
import com.codexcraft.caretap.data.repository.StorageRepository
import com.codexcraft.caretap.ui.theme.CareTapTheme
import com.codexcraft.caretap.ui.theme.Primary
import com.codexcraft.caretap.utils.ImageUtils
import java.io.File

class ProfileDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PROFILE_ID = "profile_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val profileId = intent.getStringExtra(EXTRA_PROFILE_ID)
        val repository = StorageRepository(this)

        setContent {
            CareTapTheme {
                if (profileId == null) {
                    LaunchedEffect(Unit) { finish() }
                    return@CareTapTheme
                }

                val profile = repository.loadProfiles().find { it.id == profileId }

                if (profile == null) {
                    LaunchedEffect(Unit) { finish() }
                    return@CareTapTheme
                }

                ProfileDetailScreen(
                    profile = profile,
                    repository = repository,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    profile: Profile,
    repository: StorageRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentImageUri by remember { mutableStateOf(profile.imageUri) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Haptic helper
    fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val v = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION")
            v.vibrate(50)
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { uri ->
                val savedPath = ImageUtils.compressAndSave(context, uri, profile.id)
                if (savedPath != null) {
                    repository.updateProfileImage(profile.id, savedPath)
                    currentImageUri = savedPath
                    Toast.makeText(context, "Photo saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val photoFile = File(context.cacheDir, "photo_${profile.id}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile
            )
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    // Call permission launcher
    val callPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${profile.phone}"))
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Call permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = profile.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Profile image or initials
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                if (currentImageUri.isNotEmpty()) {
                    AsyncImage(
                        model = currentImageUri,
                        contentDescription = profile.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = profile.name
                            .split(" ")
                            .take(2)
                            .joinToString("") { it.first().uppercase() },
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Text(
                text = profile.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Text(
                text = profile.phone,
                fontSize = 18.sp,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // CALL button
            ActionButton(
                emoji = "📞",
                label = "Call",
                color = Color(0xFF4CAF50),
                onClick = {
                    vibrate()
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        val intent = Intent(
                            Intent.ACTION_CALL,
                            Uri.parse("tel:${profile.phone}")
                        )
                        context.startActivity(intent)
                    } else {
                        callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                }
            )

            // WHATSAPP button
            ActionButton(
                emoji = "💬",
                label = "WhatsApp",
                color = Color(0xFF25D366),
                onClick = {
                    vibrate()
                    val phone = profile.phone
                        .replace("+", "")
                        .replace(" ", "")
                        .replace("-", "")
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$phone")
                        setPackage("com.whatsapp")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "WhatsApp not installed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

            // CAMERA button
            ActionButton(
                emoji = "📷",
                label = "Take Photo",
                color = Primary,
                onClick = {
                    vibrate()
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        val photoFile = File(context.cacheDir, "photo_${profile.id}.jpg")
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            photoFile
                        )
                        cameraImageUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )
        }
    }
}

@Composable
fun ActionButton(
    emoji: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}