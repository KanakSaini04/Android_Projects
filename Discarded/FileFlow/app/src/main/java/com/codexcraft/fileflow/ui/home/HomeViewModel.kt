package com.codexcraft.fileflow.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.core.db.entity.RecentEntity
import com.codexcraft.fileflow.domain.repository.MetadataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val metadataRepository: MetadataRepository
) : ViewModel() {
    private val _recents = MutableStateFlow<List<RecentEntity>>(emptyList())
    val recents = _recents.asStateFlow()

    init {
        viewModelScope.launch {
            metadataRepository.getRecents().collect { _recents.value = it }
        }
    }
}
