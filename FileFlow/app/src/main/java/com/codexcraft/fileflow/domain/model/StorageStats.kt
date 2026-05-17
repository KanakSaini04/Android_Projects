package com.codexcraft.fileflow.domain.model

data class StorageStats(
    val totalSpace: Long,
    val usedSpace: Long,
    val freeSpace: Long,
    val categoryBreakdown: Map<FileCategory, Long> = emptyMap()
) {
    val usedPercentage: Float
        get() = if (totalSpace > 0) (usedSpace.toFloat() / totalSpace) else 0f
}
