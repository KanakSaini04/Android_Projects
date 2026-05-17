package com.codexcraft.fileflow.presentation.reader

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.codexcraft.fileflow.domain.model.FileCategory
import com.codexcraft.fileflow.presentation.reader.renderers.ImageRenderer
import com.codexcraft.fileflow.presentation.reader.renderers.MediaPlayerRenderer
import com.codexcraft.fileflow.presentation.reader.renderers.PdfRenderer
import com.codexcraft.fileflow.presentation.reader.renderers.TextRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalReaderScreen(
    fileUri: String,
    onBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val decodedUri = remember(fileUri) { Uri.parse(Uri.decode(fileUri)) }
    val fileMetadata by viewModel.fileMetadata.collectAsState()

    LaunchedEffect(decodedUri) {
        viewModel.loadFileMetadata(decodedUri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = fileMetadata?.name ?: "Reader",
                        maxLines = 1
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (fileMetadata?.category) {
                FileCategory.PDF -> PdfRenderer(uri = decodedUri)
                FileCategory.IMAGE -> ImageRenderer(uri = decodedUri)
                FileCategory.TEXT -> TextRenderer(uri = decodedUri)
                FileCategory.VIDEO, FileCategory.AUDIO -> MediaPlayerRenderer(uri = decodedUri)
                else -> {
                    Text("Unsupported file type or loading...")
                }
            }
        }
    }
}
