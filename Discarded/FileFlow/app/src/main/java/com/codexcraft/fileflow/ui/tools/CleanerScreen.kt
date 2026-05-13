package com.codexcraft.fileflow.ui.tools

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CleanerScreen(viewModel: ToolsViewModel = hiltViewModel()) {
    var duplicates by remember { mutableStateOf(listOf<Pair<String, List<Uri>>>()) }
    var selectedForDeletion by remember { mutableStateOf(setOf<Uri>()) }
    var isScanning by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Cleaner", style = MaterialTheme.typography.headlineMedium)
        Text("Select files to delete. Original files are kept.", color = MaterialTheme.colorScheme.tertiary)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    isScanning = true
                    viewModel.findDuplicates {
                        duplicates = it
                        isScanning = false
                    }
                },
                enabled = !isScanning
            ) {
                Text(if (isScanning) "Scanning..." else "Scan Duplicates")
            }
            if (selectedForDeletion.isNotEmpty()) {
                Button(
                    onClick = {
                        viewModel.deleteFiles(selectedForDeletion.toList()) {
                            selectedForDeletion = emptySet()
                            duplicates = emptyList()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete ${selectedForDeletion.size} Files")
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        LazyColumn {
            duplicates.forEach { (groupName, uris) ->
                item {
                    Text(
                        groupName,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(uris) { uri ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = selectedForDeletion.contains(uri),
                            onCheckedChange = { checked ->
                                selectedForDeletion = if (checked) {
                                    selectedForDeletion + uri
                                } else {
                                    selectedForDeletion - uri
                                }
                            }
                        )
                        // The .weight(1f) works here because it is a direct child of Row
                        Text(
                            text = uri.lastPathSegment ?: "Unknown",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}