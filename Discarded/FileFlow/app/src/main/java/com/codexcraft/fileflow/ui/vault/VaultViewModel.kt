package com.codexcraft.fileflow.ui.vault

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.domain.repository.VaultItem
import com.codexcraft.fileflow.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository
) : ViewModel() {
    private val _items = MutableStateFlow<List<VaultItem>>(emptyList())
    val items = _items.asStateFlow()

    fun loadVault() {
        viewModelScope.launch {
            _items.value = vaultRepository.listVault()
        }
    }

    fun encryptFile(uri: Uri, name: String) {
        viewModelScope.launch {
            vaultRepository.encryptToVault(uri, name)
            loadVault()
        }
    }

    fun decryptFile(item: VaultItem, targetUri: Uri) {
        viewModelScope.launch {
            vaultRepository.decryptFromVault(item, targetUri)
            loadVault()
        }
    }
}
