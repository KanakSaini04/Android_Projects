package com.codexcraft.fileflow.ui.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
fun FlowShareScreen(viewModel: ToolsViewModel = hiltViewModel()) {
    var url by remember { mutableStateOf<String?>(null) }
    var isRunning by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("FlowShare (Local WiFi Server)", style = MaterialTheme.typography.headlineMedium)
        Text("Only use on trusted local networks.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                viewModel.startFlowShare {
                    url = it
                    isRunning = true
                }
            }, enabled = !isRunning) { Text("Start") }
            Button(onClick = {
                viewModel.stopFlowShare()
                url = null
                isRunning = false
            }, enabled = isRunning) { Text("Stop") }
        }
        if (url != null) Text("Access from browser: $url")
    }
}
