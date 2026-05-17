package com.codexcraft.fileflow.presentation.browse.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codexcraft.fileflow.domain.model.FileCategory
import com.codexcraft.fileflow.domain.model.FileItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FileListItem(
    file: FileItem,
    onFileClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onFileClick),
        headlineContent = {
            Text(
                text = file.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = "${formatDate(file.lastModified)} • ${if (file.isDirectory) "Folder" else formatBytes(file.size)}",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            Icon(
                imageVector = if (file.isDirectory) Icons.Default.Folder else getIconForCategory(file.category),
                contentDescription = null,
                tint = if (file.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        },
        trailingContent = {
            Row {
                IconButton(onClick = onFavoriteClick) {
                    Icon(Icons.Default.StarBorder, contentDescription = "Favorite")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    )
}

private fun getIconForCategory(category: FileCategory) = when (category) {
    FileCategory.PDF -> Icons.Default.PictureAsPdf
    FileCategory.DOCUMENT -> Icons.Default.Description
    FileCategory.IMAGE -> Icons.Default.Image
    FileCategory.VIDEO -> Icons.Default.VideoLibrary
    FileCategory.AUDIO -> Icons.Default.AudioFile
    FileCategory.ARCHIVE -> Icons.Default.FolderZip
    FileCategory.TEXT -> Icons.Default.TextSnippet
    FileCategory.OTHER -> Icons.Default.InsertDriveFile
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1_000_000_000 -> String.format("%.1f GB", bytes / 1_000_000_000.0)
        bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}
