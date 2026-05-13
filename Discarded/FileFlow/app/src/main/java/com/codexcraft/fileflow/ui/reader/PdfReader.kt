package com.codexcraft.fileflow.ui.reader

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun PdfReader(uri: Uri, viewModel: ReaderViewModel) {
    val pageCount by viewModel.pageCount.collectAsState()
    var page by remember { mutableStateOf(0) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var scale by remember { mutableStateOf(1f) }
    val zoomState = rememberTransformableState { zoomChange, _, _ ->
        scale *= zoomChange
    }

    LaunchedEffect(uri) {
        viewModel.loadPdf(uri)
    }

    LaunchedEffect(uri, page, pageCount) {
        if (pageCount > 0) {
            bitmap = viewModel.renderPdfPage(uri, page.coerceIn(0, pageCount - 1), 1080, 1920)
        }
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { if (page > 0) page -= 1 }) { Text("Prev") }
            Text("Page ${page + 1} / $pageCount")
            Button(onClick = { if (page < pageCount - 1) page += 1 }) { Text("Next") }
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .transformable(zoomState),
                    contentScale = ContentScale.Fit
                )
            } ?: CircularProgressIndicator()
        }
    }
}
