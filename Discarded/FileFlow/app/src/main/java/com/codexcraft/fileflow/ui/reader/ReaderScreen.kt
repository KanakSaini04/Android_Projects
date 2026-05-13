package com.codexcraft.fileflow.ui.reader

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.codexcraft.fileflow.core.util.FileType
import com.codexcraft.fileflow.core.util.UriUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    uriString: String,
    mimeString: String,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uri = remember(uriString) { Uri.parse(Uri.decode(uriString)) }
    val mime = remember(mimeString) {
        mimeString.ifEmpty { UriUtils.getMimeType(context, uri) ?: "*/*" }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(UriUtils.getFileName(context, uri)) }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                FileType.isPdf(mime) -> PdfReader(uri, viewModel)
                FileType.isImage(mime) -> ImageViewer(uri)
                FileType.isVideo(mime) || FileType.isAudio(mime) -> MediaReader(uri)
                FileType.isText(mime) -> TextEditor(uri, viewModel)
                else -> Text("Unsupported format: $mime")
            }
        }
    }
}
