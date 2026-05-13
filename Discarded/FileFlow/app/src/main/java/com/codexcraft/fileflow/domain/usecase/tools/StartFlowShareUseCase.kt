package com.codexcraft.fileflow.domain.usecase.tools

import com.codexcraft.fileflow.domain.repository.ToolsRepository
import javax.inject.Inject

class StartFlowShareUseCase @Inject constructor(
    private val repository: ToolsRepository
) {
    suspend operator fun invoke(port: Int = 8080): String = repository.startFlowShare(port)
}
