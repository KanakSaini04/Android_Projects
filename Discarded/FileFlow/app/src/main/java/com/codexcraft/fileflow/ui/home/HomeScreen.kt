package com.codexcraft.fileflow.ui.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.codexcraft.fileflow.app.navigation.Routes
import com.codexcraft.fileflow.core.db.entity.RecentEntity
import com.codexcraft.fileflow.core.util.FileType

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recents by viewModel.recents.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Home Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(32.dp))

        Text(
            "Smart Recents",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(16.dp))
        if (recents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text("No recent files yet", color = MaterialTheme.colorScheme.onSurface)
            }
        } else {
            LazyRow {
                items(recents) { item ->
                    RecentCard(item = item) {
                        val encodedUri = Uri.encode(item.uriString)
                        val encodedMime = Uri.encode(item.mimeType ?: "*/*")
                        navController.navigate(
                            Routes.READER.replace("{uri}", encodedUri).replace("{mime}", encodedMime)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
private fun RecentCard(item: RecentEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp).fillMaxSize()) {
            val icon = when {
                FileType.isPdf(item.mimeType) -> "📄 PDF"
                FileType.isImage(item.mimeType) -> "🖼 IMG"
                else -> "📁 File"
            }
            Text(icon, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            Text(item.name, style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }
    }
}
