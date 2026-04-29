package com.example.taskflow.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskflow.presentation.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    viewModel: TaskViewModel,
    onBack: () -> Unit,
    onAddNote: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val task = tasks.find { it.id == taskId }
    val notes by viewModel.getNotesForTask(taskId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task?.title ?: "Task Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            task?.let {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Priority", style = MaterialTheme.typography.labelSmall)
                        PriorityLabel(it.priority)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Status: ${if (it.isCompleted) "Completed" else "Pending"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Text("Notes", style = MaterialTheme.typography.titleMedium)

            if (notes.isEmpty()) {
                Text("No notes yet. Tap + to add one.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notes) { note ->
                        var showNoteDeleteDialog by remember { mutableStateOf(false) }

                        if (showNoteDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showNoteDeleteDialog = false },
                                title = { Text("Delete Note") },
                                text = { Text("Are you sure you want to delete this note?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showNoteDeleteDialog = false
                                        viewModel.deleteNote(note)
                                    }) {
                                        Text("Delete", color = Color.Red)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showNoteDeleteDialog = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    note.title?.let {
                                        Text(it, style = MaterialTheme.typography.titleSmall)
                                    }
                                    Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = { showNoteDeleteDialog = true }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Note",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}