package com.codexcraft.caretap.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.codexcraft.caretap.data.model.Profile
import com.codexcraft.caretap.data.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared ViewModel — survives screen rotation.
 * Extends AndroidViewModel so it can hold an Application context
 * safely (needed to instantiate StorageRepository).
 *
 * Consumed by: HomeScreen, AppsScreen, ProfileDetailActivity.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StorageRepository(application)

    // UI observes this — emits a fresh sorted list after every change
    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    init {
        loadProfiles()
    }

    // ─────────────────────────────────────────────
    // PUBLIC API called from Composables
    // ─────────────────────────────────────────────

    fun loadProfiles() {
        _profiles.value = repository.getProfiles()   // already sorted by usageCount
    }

    fun addProfile(profile: Profile) {
        repository.addProfile(profile)
        loadProfiles()
    }

    fun updateProfile(profile: Profile) {
        repository.updateProfile(profile)
        loadProfiles()
    }

    fun deleteProfile(profileId: String) {
        repository.deleteProfile(profileId)
        loadProfiles()
    }

    /**
     * Call this whenever the user taps a profile tile.
     * The grid will re-order automatically on next load.
     */
    fun onProfileTapped(profileId: String) {
        repository.incrementUsage(profileId)
        loadProfiles()
    }
}