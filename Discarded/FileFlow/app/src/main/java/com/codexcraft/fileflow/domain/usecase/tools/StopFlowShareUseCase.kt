package com.codexcraft.fileflow.domain.usecase.tools

import com.codexcraft.fileflow.domain.repository.ToolsRepository
import javax.inject.Inject

class StopFlowShareUseCase @Inject constructor(
    private val repository: ToolsRepository
) {
    suspend operator fun invoke() = repository.stopFlowShare()
}
