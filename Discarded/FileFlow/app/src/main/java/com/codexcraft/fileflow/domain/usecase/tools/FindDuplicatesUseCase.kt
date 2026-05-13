package com.codexcraft.fileflow.domain.usecase.tools

import android.net.Uri
import com.codexcraft.fileflow.domain.repository.ToolsRepository
import javax.inject.Inject

class FindDuplicatesUseCase @Inject constructor(
    private val repository: ToolsRepository
) {
    suspend operator fun invoke(): List<Pair<String, List<Uri>>> = repository.findDuplicateCandidates()
}
