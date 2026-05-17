package com.codexcraft.fileflow.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codexcraft.fileflow.presentation.home.components.QuickTiles
import com.codexcraft.fileflow.presentation.home.components.SmartRecentCarousel
import com.codexcraft.fileflow.presentation.home.components.StorageHeatmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToReader: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val storageStats by viewModel.storageStats.collectAsState()
    val recentFiles by viewModel.recentFiles.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FileFlow") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Storage Overview",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            item {
                StorageHeatmap(stats = storageStats)
            }
            
            item {
                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                QuickTiles(
                    onTileClick = { category ->
                        // Navigate to browse with filter
                    }
                )
            }
            
            item {
                Text(
                    text = "Recent Files",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                SmartRecentCarousel(
                    files = recentFiles,
                    onFileClick = { fileUri ->
                        onNavigateToReader(fileUri.toString())
                    }
                )
            }
        }
    }
}
