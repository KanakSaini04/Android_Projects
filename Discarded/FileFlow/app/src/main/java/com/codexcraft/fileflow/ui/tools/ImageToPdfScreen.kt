package com.codexcraft.fileflow.ui.tools

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ImageToPdfScreen(viewModel: ToolsViewModel = hiltViewModel()) {
    var images by remember { mutableStateOf(listOf<Uri>()) }
    var outputUri by remember { mutableStateOf<Uri?>(null) }
    var isWorking by remember { mutableStateOf(false) }

    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) images = images + uris
    }

    val pickOutput = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        outputUri = uri
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { pickImages.launch(arrayOf("image/*")) }) { Text("Add Images") }
            Button(onClick = { pickOutput.launch("FileFlow_Output.pdf") }, enabled = images.isNotEmpty()) {
                Text("Choose Output")
            }
            Button(
                onClick = {
                    val out = outputUri ?: return@Button
                    isWorking = true
                    viewModel.createPdf(images, out) { isWorking = false }
                },
                enabled = images.isNotEmpty() && outputUri != null && !isWorking
            ) { Text(if (isWorking) "Creating..." else "Create PDF") }
        }

        Spacer(Modifier.height(12.dp))

        // weight(1f) works here because it's inside ColumnScope
        LazyColumn(Modifier.weight(1f)) {
            itemsIndexed(images) { index, uri ->
                Row(Modifier.padding(vertical = 8.dp)) {
                    // weight(1f) works here because it's inside RowScope
                    Text("${index + 1}. ${uri.lastPathSegment}", modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        if (index > 0) {
                            images = images.toMutableList().apply { add(index - 1, removeAt(index)) }
                        }
                    }) { Icon(Icons.Default.ArrowUpward, null) }
                    IconButton(onClick = {
                        if (index < images.size - 1) {
                            images = images.toMutableList().apply { add(index + 1, removeAt(index)) }
                        }
                    }) { Icon(Icons.Default.ArrowDownward, null) }
                }
                Divider()
            }
        }
    }
}