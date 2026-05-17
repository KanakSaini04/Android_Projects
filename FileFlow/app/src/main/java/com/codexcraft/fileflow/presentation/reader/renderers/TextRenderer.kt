package com.codexcraft.fileflow.presentation.reader.renderers

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TextRenderer(uri: Uri) {
    val context = LocalContext.current
    var textContent by remember { mutableStateOf("Loading...") }

    LaunchedEffect(uri) {
        textContent = withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader().use { it.readText() }
                } ?: "Error: Could not open file"
            } catch (e: Exception) {
                "Error reading file: ${e.message}"
            }
        }
    }

    Text(
        text = textContent,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        style = MaterialTheme.typography.bodyMedium
    )
}
