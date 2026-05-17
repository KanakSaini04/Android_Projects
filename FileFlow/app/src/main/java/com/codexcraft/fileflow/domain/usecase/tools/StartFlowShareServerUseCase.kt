package com.codexcraft.fileflow.domain.usecase.tools

import com.codexcraft.fileflow.domain.repository.ToolsRepository
import javax.inject.Inject

class StartFlowShareServerUseCase @Inject constructor(
    private val repository: ToolsRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.startFlowShareServer()
    }
}
