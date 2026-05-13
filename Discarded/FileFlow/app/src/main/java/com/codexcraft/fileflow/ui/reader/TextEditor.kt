package com.codexcraft.fileflow.ui.reader

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextEditor(uri: Uri, viewModel: ReaderViewModel) {
    val text by viewModel.text.collectAsState()
    var draft by remember(text) { mutableStateOf(text) }

    LaunchedEffect(uri) { viewModel.loadText(uri) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it },
            // Explicitly called within ColumnScope
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = { Text("Edit text/markdown...") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { viewModel.saveText(uri, draft) }) {
                Text("Save")
            }
        }
    }
}