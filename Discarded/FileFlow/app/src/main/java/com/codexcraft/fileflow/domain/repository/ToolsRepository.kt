package com.codexcraft.fileflow.domain.repository

import android.net.Uri

interface ToolsRepository {
    suspend fun createPdfFromImages(imageUris: List<Uri>, outputUri: Uri): Boolean
    suspend fun findDuplicateCandidates(): List<Pair<String, List<Uri>>>
    suspend fun findCacheCandidates(): List<Uri>
    suspend fun deleteFiles(uris: List<Uri>): Int
    suspend fun startFlowShare(port: Int = 8080): String
    suspend fun stopFlowShare()
}
