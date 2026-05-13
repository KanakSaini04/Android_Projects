package com.codexcraft.fileflow.ui.browse

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.codexcraft.fileflow.app.navigation.Routes
import com.codexcraft.fileflow.core.util.UriUtils
import com.codexcraft.fileflow.domain.model.FileItem

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    navController: NavController,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var selected by remember { mutableStateOf<FileItem?>(null) }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.load(uri, UriUtils.getFileName(context, uri))
        }
    }

    val targetPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null && selected != null) {
            viewModel.moveSelected(selected!!.uri, uri)
            selected = null
        }
    }

    if (state.showCreateFolderDialog) {
        InputDialog(
            title = "Create Folder",
            initial = "Folder Name",
            onDismiss = { viewModel.toggleCreateDialog(false) },
            onConfirm = { viewModel.createFolder(it) }
        )
    }

    state.itemToRename?.let { item ->
        InputDialog(
            title = "Rename",
            initial = item.name,
            onDismiss = { viewModel.setRenameItem(null) },
            onConfirm = { viewModel.renameItem(item, it) }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (state.currentParent != null) {
                FloatingActionButton(onClick = { viewModel.toggleCreateDialog(true) }) {
                    Icon(Icons.Default.Add, "Create Folder")
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(onClick = { folderPicker.launch(null) }) { Text("Select Root Folder") }
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.breadcrumbs) { crumb ->
                    Text(
                        "${crumb.second} >",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { viewModel.load(crumb.first, crumb.second) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.items, key = { it.uri.toString() }) { item ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (item.isDirectory) {
                                        viewModel.load(item.uri, item.name)
                                    } else {
                                        viewModel.openFile(item)
                                        navController.navigate(
                                            "reader?uri=${Uri.encode(item.uri.toString())}&mime=${Uri.encode(item.mimeType ?: "*/*")}"
                                        )
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (item.isDirectory) "📁" else "📄", modifier = Modifier.width(32.dp))
                            Text(item.name, modifier = Modifier.weight(1f))
                            if (!item.isDirectory) {
                                IconButton(onClick = { viewModel.setRenameItem(item) }) {
                                    Icon(Icons.Default.Edit, "Rename")
                                }
                                IconButton(onClick = { selected = item; targetPicker.launch(null) }) {
                                    Icon(Icons.Default.DriveFileMove, "Move")
                                }
                                IconButton(onClick = { viewModel.deleteSelected(item) }) {
                                    Icon(Icons.Default.Delete, "Delete")
                                }
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun InputDialog(title: String, initial: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onConfirm(text) }) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
