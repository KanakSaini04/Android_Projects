package com.example.liquidcalc.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import com.example.liquidcalc.ui.components.ButtonType
import com.example.liquidcalc.ui.components.GlassButton
import com.example.liquidcalc.ui.components.GlassPanel
import com.example.liquidcalc.viewmodel.CalculatorViewModel

private data class CalcKey(
    val label: String,
    val type: ButtonType,
    val span: Int = 1
)

private val buttonRows = listOf(
    listOf(
        CalcKey("AC", ButtonType.FUNCTION),
        CalcKey("+/-", ButtonType.FUNCTION),
        CalcKey("%", ButtonType.FUNCTION),
        CalcKey("÷", ButtonType.OPERATOR)
    ),
    listOf(
        CalcKey("7", ButtonType.NUMBER),
        CalcKey("8", ButtonType.NUMBER),
        CalcKey("9", ButtonType.NUMBER),
        CalcKey("×", ButtonType.OPERATOR)
    ),
    listOf(
        CalcKey("4", ButtonType.NUMBER),
        CalcKey("5", ButtonType.NUMBER),
        CalcKey("6", ButtonType.NUMBER),
        CalcKey("-", ButtonType.OPERATOR)
    ),
    listOf(
        CalcKey("1", ButtonType.NUMBER),
        CalcKey("2", ButtonType.NUMBER),
        CalcKey("3", ButtonType.NUMBER),
        CalcKey("+", ButtonType.OPERATOR)
    ),
    listOf(
        CalcKey("⌫", ButtonType.FUNCTION),
        CalcKey("0", ButtonType.NUMBER),
        CalcKey(".", ButtonType.NUMBER),
        CalcKey("=", ButtonType.EQUALS)
    )
)

// ── GlassIconButton defined FIRST so it can be used below ────────────────────
@Composable
private fun GlassIconButton(
    size: Dp = 42.dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.15f),
        tonalElevation = 0.dp,
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val expression by viewModel.expression.collectAsState()
    val display by viewModel.display.collectAsState()
    val history by viewModel.history.collectAsState()
    val backgroundUri by viewModel.backgroundUri.collectAsState()

    var showHistory by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.saveBackground(uri)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background ───────────────────────────────────────────────────────
        if (backgroundUri != null) {
            val colorMatrix = ColorMatrix(
                floatArrayOf(
                    0.45f, 0f, 0f, 0f, -50f,
                    0f, 0.45f, 0f, 0f, -50f,
                    0f, 0f, 0.45f, 0f, -50f,
                    0f, 0f, 0f, 1f,   0f
                )
            )

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(Uri.parse(backgroundUri))
                    .crossfade(true)
                    .build(),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Lightness & lightrange -50 via alpha blending with black
                        renderEffect = null
                    }
                    .drawWithContent {
                        drawContent()
                        // Lightness overlay: semi-transparent black layer
                        drawRect(
                            color = Color.Black.copy(alpha = 0.45f)
                        )
                    }
                ,
                colorFilter = ColorFilter.colorMatrix(colorMatrix)
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1C1C2E),
                            Color(0xFF0D0D1A)
                        )
                    )
                )
            }
        }

        // ── Main UI ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ── Top Bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(onClick = { showHistory = true }) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = "History",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                GlassIconButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Set background",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // ── Display ──────────────────────────────────────────────────────
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                cornerRadius = 28.dp,
                backgroundAlpha = 0.14f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 55.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = expression.ifEmpty { "" },
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.50f),
                        fontWeight = FontWeight.Light,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val displayFontSize = when {
                        display.length > 14 -> 28.sp
                        display.length > 9  -> 38.sp
                        else                -> 52.sp
                    }
                    Text(
                        text = display,
                        fontSize = displayFontSize,
                        color = Color.White,
                        fontWeight = FontWeight.Thin,
                        letterSpacing = (-1).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Button Grid ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                buttonRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { key ->
                            GlassButton(
                                label = key.label,
                                type = key.type,
                                modifier = Modifier
                                    .weight(key.span.toFloat())
                                    .aspectRatio(1f),
                                fontSize = when (key.label) {
                                    "AC", "+/-", "%" -> 18.sp
                                    "÷", "×", "-", "+" -> 26.sp
                                    "=" -> 28.sp
                                    "⌫" -> 22.sp
                                    else -> 24.sp
                                },
                                onClick = { viewModel.onButton(key.label) }
                            )
                        }
                    }
                }
            }
        }

        // ── History Panel ────────────────────────────────────────────────────
        HistoryPanel(
            visible = showHistory,
            history = history,
            onEntryClick = { viewModel.restoreFromHistory(it) },
            onClearAll = { viewModel.clearHistory() },
            onDismiss = { showHistory = false }
        )
    }
}