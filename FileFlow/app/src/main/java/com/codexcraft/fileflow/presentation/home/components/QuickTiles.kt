package com.codexcraft.fileflow.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.codexcraft.fileflow.domain.model.FileCategory
import com.codexcraft.fileflow.presentation.theme.*

@Composable
fun QuickTiles(onTileClick: (FileCategory) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickTile(
                icon = Icons.Default.PictureAsPdf,
                label = "PDFs",
                color = PdfRed,
                modifier = Modifier.weight(1f),
                onClick = { onTileClick(FileCategory.PDF) }
            )
            QuickTile(
                icon = Icons.Default.Description,
                label = "Documents",
                color = DocBlue,
                modifier = Modifier.weight(1f),
                onClick = { onTileClick(FileCategory.DOCUMENT) }
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickTile(
                icon = Icons.Default.PlayCircle,
                label = "Media",
                color = MediaPurple,
                modifier = Modifier.weight(1f),
                onClick = { onTileClick(FileCategory.VIDEO) }
            )
            QuickTile(
                icon = Icons.Default.FolderZip,
                label = "Archives",
                color = ZipOrange,
                modifier = Modifier.weight(1f),
                onClick = { onTileClick(FileCategory.ARCHIVE) }
            )
        }
    }
}

@Composable
private fun QuickTile(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
        }
    }
}
