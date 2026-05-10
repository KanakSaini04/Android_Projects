package com.example.qrforge.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.example.qrforge.ui.theme.*
import com.example.qrforge.ui.viewmodel.ScanViewModel
import java.util.concurrent.Executors

data class ScanResult(val rawValue: String, val type: String, val format: String)

fun detectType(barcode: Barcode): String = when (barcode.valueType) {
    Barcode.TYPE_URL          -> "URL"
    Barcode.TYPE_EMAIL        -> "Email"
    Barcode.TYPE_PHONE        -> "Phone"
    Barcode.TYPE_SMS          -> "SMS"
    Barcode.TYPE_WIFI         -> "WiFi"
    Barcode.TYPE_CONTACT_INFO -> "Contact"
    else                      -> "Text"
}

fun scanImageFromUri(context: Context, uri: Uri, onResult: (ScanResult) -> Unit) {
    try {
        val image = InputImage.fromFilePath(context, uri)
        BarcodeScanning.getClient().process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.let { bc ->
                    bc.rawValue?.let { value ->
                        onResult(ScanResult(value, detectType(bc), bc.format.toString()))
                    }
                } ?: Toast.makeText(context, "No QR code detected", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to scan image", Toast.LENGTH_SHORT).show()
            }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not load image", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(viewModel: ScanViewModel) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var isGalleryMode by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var scannedResult by remember { mutableStateOf<ScanResult?>(null) }
    var flashEnabled by remember { mutableStateOf(false) }
    var zoomLevel by remember { mutableStateOf(0f) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            scanImageFromUri(context, it) { result ->
                scannedResult = result
                viewModel.saveToHistory(result.rawValue, result.type, result.format)
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row {
                    Text("QR", style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold)
                    Text("Forge", style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.ExtraBold)
                }
                Text("Scan QR Code", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }

            // Flashlight button in header
            IconButton(
                onClick = { flashEnabled = !flashEnabled },
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                    .background(
                        if (flashEnabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    if (flashEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                    null,
                    tint = if (flashEnabled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.primary
                )
            }
        }

        // Mode Switcher
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp)
        ) {
            listOf(false to "Camera", true to "Gallery").forEach { (isGallery, label) ->
                val selected = isGalleryMode == isGallery
                Box(
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable {
                            isGalleryMode = isGallery
                            selectedImageUri = null
                            scannedResult = null
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            if (isGallery) Icons.Outlined.Image else Icons.Outlined.CameraAlt,
                            null,
                            tint = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground.copy(0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground.copy(0.5f),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Camera / Gallery Frame
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    1.5.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    RoundedCornerShape(24.dp)
                )
        ) {
            if (isGalleryMode) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Outlined.AddPhotoAlternate, null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(56.dp))
                            Text("Tap to select image",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                        }
                    }
                }
            } else {
                if (cameraPermission.status.isGranted) {
                    CameraPreview(
                        flashEnabled = flashEnabled,
                        onCameraReady = { cameraControl = it },
                        onZoomChange = { delta ->
                            zoomLevel = (zoomLevel + delta).coerceIn(0f, 1f)
                            cameraControl?.setLinearZoom(zoomLevel)
                        },
                        onQrScanned = { result ->
                            if (scannedResult == null) {
                                scannedResult = result
                                viewModel.saveToHistory(result.rawValue, result.type, result.format)
                                if (settings.autoOpenUrls && result.type == "URL") {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(result.rawValue))
                                    )
                                }
                            }
                        }
                    )
                    ScanLineAnimation()
                    ScannerCorners(color = MaterialTheme.colorScheme.primary)
                } else {
                    Box(
                        Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(Icons.Outlined.CameraAlt, null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(56.dp))
                            Text("Camera Permission Required",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground)
                            Button(
                                onClick = { cameraPermission.launchPermissionRequest() },
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Grant Permission") }
                        }
                    }
                }
            }
        }

        // Zoom Slider
        AnimatedVisibility(visible = !isGalleryMode && cameraPermission.status.isGranted) {
            Row(
                Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.ZoomOut, null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(0.4f),
                    modifier = Modifier.size(18.dp))
                Slider(
                    value = zoomLevel,
                    onValueChange = {
                        zoomLevel = it
                        cameraControl?.setLinearZoom(it)
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Icon(Icons.Filled.ZoomIn, null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(0.4f),
                    modifier = Modifier.size(18.dp))
            }
        }

        if (scannedResult == null) {
            Text(
                if (isGalleryMode) "Select an image containing a QR or barcode"
                else "Point camera at any QR code or barcode",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    scannedResult?.let { result ->
        ScanResultSheet(
            result = result,
            onDismiss = { scannedResult = null },
            context = context
        )
    }
}

@Composable
fun ScanLineAnimation() {
    val transition = rememberInfiniteTransition(label = "scan")
    val offsetY by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "y"
    )
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier.fillMaxWidth().height(2.dp)
                .align(Alignment.TopStart)
                .offset(y = (offsetY * 260).dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(0.8f),
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(0.8f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun ScannerCorners(color: Color) {
    val size = 36.dp
    val padding = 16.dp
    val stroke = 3.dp
    Box(Modifier.fillMaxSize()) {
        listOf(
            Alignment.TopStart    to RoundedCornerShape(topStart = 12.dp),
            Alignment.TopEnd      to RoundedCornerShape(topEnd = 12.dp),
            Alignment.BottomStart to RoundedCornerShape(bottomStart = 12.dp),
            Alignment.BottomEnd   to RoundedCornerShape(bottomEnd = 12.dp),
        ).forEach { (align, shape) ->
            Box(
                Modifier.align(align).padding(padding).size(size)
                    .border(stroke, color, shape)
            )
        }
    }
}

@Composable
fun CameraPreview(
    flashEnabled: Boolean,
    onCameraReady: (CameraControl) -> Unit,
    onZoomChange: (Float) -> Unit,
    onQrScanned: (ScanResult) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    LaunchedEffect(flashEnabled) { camera?.cameraControl?.enableTorch(flashEnabled) }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ -> onZoomChange(zoom - 1f) }
        }
    ) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val scanner = BarcodeScanning.getClient()
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { proxy ->
                val img = proxy.image
                if (img != null) {
                    val input = InputImage.fromMediaImage(img, proxy.imageInfo.rotationDegrees)
                    scanner.process(input)
                        .addOnSuccessListener { barcodes ->
                            barcodes.firstOrNull()?.let { bc ->
                                bc.rawValue?.let { value ->
                                    onQrScanned(
                                        ScanResult(value, detectType(bc), bc.format.toString())
                                    )
                                }
                            }
                        }
                        .addOnCompleteListener { proxy.close() }
                } else proxy.close()
            }
            try {
                provider.unbindAll()
                camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview, analysis
                )
                camera?.cameraControl?.let { onCameraReady(it) }
            } catch (e: Exception) { e.printStackTrace() }
        }, ContextCompat.getMainExecutor(context))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultSheet(result: ScanResult, onDismiss: () -> Unit, context: Context) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val typeColor = qrTypeColors[result.type] ?: MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp))
                        .background(typeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(result.type, style = MaterialTheme.typography.labelSmall,
                        color = typeColor, fontWeight = FontWeight.Bold)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(result.format, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                }
            }

            Text("Scanned Result", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

            Surface(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(result.rawValue, modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 6, overflow = TextOverflow.Ellipsis)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("QRForge", result.rawValue))
                        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.3f))
                ) {
                    Icon(Icons.Outlined.ContentCopy, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Copy")
                }

                if (result.type == "URL") {
                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(result.rawValue))
                            )
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.OpenInBrowser, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Open")
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Done") }
                }
            }
        }
    }
}