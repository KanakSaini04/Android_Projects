package com.codexcraft.caretap.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.caretap.data.model.Profile
import com.codexcraft.caretap.data.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StorageRepository(application)

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles

    init {
        loadProfiles()
    }

    fun loadProfiles() {
        viewModelScope.launch {
            _profiles.value = repository.loadProfiles()
        }
    }

    fun addProfile(name: String, phone: String) {
        val profile = Profile(
            id = UUID.randomUUID().toString(),
            name = name,
            phone = phone
        )
        repository.addProfile(profile)
        loadProfiles()
    }

    fun deleteProfile(id: String) {
        repository.deleteProfile(id)
        loadProfiles()
    }

    fun incrementUsage(id: String) {
        repository.incrementUsage(id)
        loadProfiles()
    }

    fun updateProfileImage(id: String, imageUri: String) {
        repository.updateProfileImage(id, imageUri)
        loadProfiles()
    }
}