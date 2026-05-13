package com.example.qrforge.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.qrforge.data.local.ScanHistoryEntity
import com.example.qrforge.ui.theme.qrTypeColors
import com.example.qrforge.ui.viewmodel.HistoryViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val filterTypes = listOf("ALL", "URL", "Text", "Email", "Phone", "SMS", "WiFi", "Contact")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showClearDialog by remember { mutableStateOf(false) }
    val history by viewModel.filteredHistory(searchQuery, selectedFilter)
        .collectAsState(initial = emptyList())
    val context = LocalContext.current

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("History", style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground)
                Text("${history.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.4f))
            }
            if (history.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Export CSV
                    IconButton(
                        onClick = {
                            viewModel.exportHistory(history) { csv ->
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/csv"
                                    val file = java.io.File(context.cacheDir, "qrforge_history.csv")
                                    file.writeText(csv)
                                    val uri = androidx.core.content.FileProvider.getUriForFile(
                                        context, "${context.packageName}.provider", file
                                    )
                                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(
                                    android.content.Intent.createChooser(intent, "Export History CSV")
                                )
                            }
                        },
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Outlined.FileDownload, null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                    // Clear all
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Outlined.DeleteSweep, null,
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text("Search history...",
                    color = MaterialTheme.colorScheme.onBackground.copy(0.35f))
            },
            leadingIcon = {
                Icon(Icons.Outlined.Search, null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(0.4f))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Clear, null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(0.4f))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            )
        )

        Spacer(Modifier.height(12.dp))

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterTypes) { type ->
                FilterChip(
                    selected = selectedFilter == type,
                    onClick = { selectedFilter = type },
                    label = { Text(type, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // List or Empty
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        if (searchQuery.isNotEmpty()) Icons.Outlined.SearchOff
                        else Icons.Outlined.History,
                        null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(0.2f),
                        modifier = Modifier.size(72.dp)
                    )
                    Text(
                        if (searchQuery.isNotEmpty()) "No results for \"$searchQuery\""
                        else "No scan history yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.4f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (searchQuery.isNotEmpty()) "Try a different search term"
                        else "Scanned and generated QR codes\nwill appear here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.25f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    SwipeableHistoryItem(
                        item = item,
                        onDelete = { viewModel.deleteItem(item) },
                        onToggleFavorite = { viewModel.toggleFavorite(item) },
                        onCopy = {
                            val cm = context.getSystemService(
                                android.content.Context.CLIPBOARD_SERVICE
                            ) as android.content.ClipboardManager
                            cm.setPrimaryClip(
                                android.content.ClipData.newPlainText("QRForge", item.rawValue)
                            )
                            android.widget.Toast.makeText(
                                context, "Copied!", android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History") },
            text = { Text("This will permanently delete all scan history.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearAll(); showClearDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Clear All", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableHistoryItem(
    item: ScanHistoryEntity,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, null,
                    tint = Color.White, modifier = Modifier.padding(end = 24.dp))
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        HistoryItemCard(item = item, onToggleFavorite = onToggleFavorite, onCopy = onCopy)
    }
}

@Composable
fun HistoryItemCard(
    item: ScanHistoryEntity,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit
) {
    val typeColor = qrTypeColors[item.type] ?: MaterialTheme.colorScheme.primary
    val fmt = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
    val dateTime = try {
        LocalDateTime.parse(item.timestamp).format(fmt)
    } catch (e: Exception) { item.timestamp }

    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                    .background(typeColor.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (item.type) {
                    "URL"     -> Icons.Outlined.Language
                    "Email"   -> Icons.Outlined.Email
                    "Phone"   -> Icons.Outlined.Phone
                    "SMS"     -> Icons.Outlined.Sms
                    "WiFi"    -> Icons.Outlined.Wifi
                    "Contact" -> Icons.Outlined.Person
                    else      -> Icons.Outlined.TextFields
                }
                Icon(icon, null, tint = typeColor, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        Modifier.clip(RoundedCornerShape(6.dp))
                            .background(typeColor.copy(0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(item.type, style = MaterialTheme.typography.labelSmall,
                            color = typeColor, fontWeight = FontWeight.Bold)
                    }
                    if (item.isGenerated) {
                        Box(
                            Modifier.clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Generated",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(item.rawValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(3.dp))
                Text(dateTime, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                    Icon(
                        if (item.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        null,
                        tint = if (item.isFavorite) Color(0xFFFFD700)
                        else MaterialTheme.colorScheme.onSurface.copy(0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.ContentCopy, null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.3f),
                        modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailSheet(
    item: ScanHistoryEntity,
    onDismiss: () -> Unit,
    context: android.content.Context
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val typeColor = qrTypeColors[item.type] ?: MaterialTheme.colorScheme.primary
    val fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy  HH:mm")
    val dateTime = try {
        LocalDateTime.parse(item.timestamp).format(fmt)
    } catch (e: Exception) { item.timestamp }

    // Generate QR bitmap
    val qrBitmap = remember(item.rawValue) {
        try {
            val hints = mapOf(com.google.zxing.EncodeHintType.MARGIN to 1)
            val matrix = com.google.zxing.MultiFormatWriter().encode(
                item.rawValue,
                com.google.zxing.BarcodeFormat.QR_CODE,
                512, 512, hints
            )
            val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            for (x in 0 until 512) for (y in 0 until 512)
                bmp.setPixel(x, y,
                    if (matrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                )
            bmp
        } catch (e: Exception) { null }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Type + format badges
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp))
                        .background(typeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(item.type,
                        style = MaterialTheme.typography.labelSmall,
                        color = typeColor, fontWeight = FontWeight.Bold)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(item.format,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                }
                if (item.isGenerated) {
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Generated",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // QR Code preview
            qrBitmap?.let { bmp ->
                Card(
                    Modifier.size(220.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Image(
                        bmp.asImageBitmap(), null,
                        modifier = Modifier.fillMaxSize().padding(12.dp)
                    )
                }
            }

            // Raw value
            Surface(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    item.rawValue,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 4,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Timestamp
            Text(
                dateTime,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(0.4f)
            )

            // Action buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Copy
                OutlinedButton(
                    onClick = {
                        val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                                as android.content.ClipboardManager
                        cm.setPrimaryClip(
                            android.content.ClipData.newPlainText("QRForge", item.rawValue)
                        )
                        android.widget.Toast.makeText(context, "Copied!", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.3f))
                ) {
                    Icon(Icons.Outlined.ContentCopy, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Copy")
                }

                // Open URL
                if (item.type == "URL") {
                    Button(
                        onClick = {
                            context.startActivity(
                                android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(item.rawValue)
                                )
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

            // Share QR image
            OutlinedButton(
                onClick = {
                    qrBitmap?.let { bmp ->
                        try {
                            val cachePath = java.io.File(context.cacheDir, "qrforge_history_share.png")
                            java.io.FileOutputStream(cachePath).use {
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, it)
                            }
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                context, "${context.packageName}.provider", cachePath
                            )
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(
                                android.content.Intent.createChooser(intent, "Share QR Code")
                            )
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Failed to share", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.3f))
            ) {
                Icon(Icons.Outlined.Share, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Share QR Image")
            }
        }
    }
}