package com.codexcraft.lensora.ui.gallery

import android.app.Activity
import android.view.WindowManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.codexcraft.lensora.core.theme.*
import com.codexcraft.lensora.data.model.CapturedMedia

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val mediaList by viewModel.mediaList.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Apply FLAG_SECURE when screen is active
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val window = (context as? Activity)?.window
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadMedia()
    }

    // Show biometric prompt when locked
    LaunchedEffect(authState) {
        if (authState is GalleryAuthState.Locked && activity != null) {
            showBiometricPrompt(
                activity = activity,
                onSuccess = { viewModel.onBiometricSuccess() },
                onFailed = { viewModel.onBiometricFailed() }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack)
    ) {
        when (authState) {
            GalleryAuthState.Locked, GalleryAuthState.Failed -> {
                LockedState(
                    isFailed = authState is GalleryAuthState.Failed,
                    onRetry = {
                        if (activity != null) {
                            showBiometricPrompt(
                                activity = activity,
                                onSuccess = { viewModel.onBiometricSuccess() },
                                onFailed = { viewModel.onBiometricFailed() }
                            )
                        }
                    }
                )
            }
            GalleryAuthState.Unlocked -> {
                AnimatedVisibility(visible = true, enter = fadeIn()) {
                    if (mediaList.isEmpty()) {
                        EmptyVaultState()
                    } else {
                        MediaGrid(mediaList = mediaList)
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedState(isFailed: Boolean, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = "Locked",
            tint = ElectricBlue,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "BIOMETRIC VAULT",
            style = LensoraTypography.labelLarge.copy(letterSpacing = 3.sp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isFailed) "Authentication failed. Try again." else "Authenticate to access your media.",
            style = LensoraTypography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        Spacer(Modifier.height(32.dp))
        OutlinedButton(
            onClick = onRetry,
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue)
        ) {
            Text("AUTHENTICATE", style = LensoraTypography.labelLarge)
        }
    }
}

@Composable
private fun EmptyVaultState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Photo,
            contentDescription = "Empty",
            tint = ElectricBlue.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Your vault is empty.",
            style = LensoraTypography.titleMedium.copy(color = TextSecondary)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Time to create.",
            style = LensoraTypography.bodyMedium.copy(color = ElectricBlue.copy(alpha = 0.5f))
        )
    }
}

@Composable
private fun MediaGrid(mediaList: List<CapturedMedia>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "VAULT  ·  ${mediaList.size}",
                style = LensoraTypography.labelLarge.copy(letterSpacing = 3.sp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(mediaList, key = { it.id }) { media ->
                Box(
                    modifier = Modifier
                        .aspectRatio(9f / 16f)
                        .background(SurfaceCard)
                ) {
                    AsyncImage(
                        model = media.file,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // AI mode badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(4.dp)
                            .background(MatteBlack.copy(0.7f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = media.aiMode,
                            style = LensoraTypography.labelSmall.copy(fontSize = 8.sp)
                        )
                    }
                }
            }
        }
    }
}

private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onFailed: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onFailed()
            }

            override fun onAuthenticationFailed() {
                onFailed()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Lensora Vault")
        .setSubtitle("Authenticate to access your secured media")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()

    biometricPrompt.authenticate(promptInfo)
}