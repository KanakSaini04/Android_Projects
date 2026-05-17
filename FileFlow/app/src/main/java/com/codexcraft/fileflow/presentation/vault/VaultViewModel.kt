package com.codexcraft.fileflow.presentation.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.domain.model.VaultFile
import com.codexcraft.fileflow.data.local.db.dao.VaultEntryDao
import com.codexcraft.fileflow.data.local.encryption.VaultCryptoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultEntryDao: VaultEntryDao,
    private val vaultCryptoManager: VaultCryptoManager
) : ViewModel() {

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    val vaultFiles: StateFlow<List<VaultFile>> = vaultEntryDao.getAllVaultEntries()
        .map { entities ->
            entities.map { entity ->
                VaultFile(
                    id = entity.id,
                    originalName = entity.originalName,
                    encryptedPath = entity.encryptedPath,
                    size = entity.size,
                    mimeType = entity.mimeType,
                    encryptedAt = entity.encryptedAt,
                    iv = entity.iv
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun unlock() {
        _isLocked.value = false
    }

    fun lock() {
        _isLocked.value = true
    }

    fun deleteFile(file: VaultFile) {
        viewModelScope.launch {
            if (vaultCryptoManager.deleteEncryptedFile(file.encryptedPath)) {
                // Remove from DB if needed, but usually dao delete
                // vaultEntryDao.deleteVaultEntry(...)
            }
        }
    }
}
