package com.codexcraft.fileflow.presentation.tools.features

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codexcraft.fileflow.presentation.tools.ToolsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowShareScreen(
    onBack: () -> Unit,
    viewModel: ToolsViewModel = hiltViewModel()
) {
    var isServerRunning by remember { mutableStateOf(false) }
    var serverUrl by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FlowShare") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = if (isServerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = if (isServerRunning) "Server is Live!" else "Transfer Files via Wi-Fi",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isServerRunning) 
                    "Visit this URL on your PC:\n$serverUrl" 
                else 
                    "Share your files with any device on the same Wi-Fi network.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = { 
                    isServerRunning = !isServerRunning
                    if (isServerRunning) serverUrl = "http://192.168.1.5:8080"
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (isServerRunning) "Stop Server" else "Start Sharing")
            }
        }
    }
}
