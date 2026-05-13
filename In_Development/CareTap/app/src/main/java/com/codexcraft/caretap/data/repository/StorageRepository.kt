package com.codexcraft.caretap.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.codexcraft.caretap.data.model.Profile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StorageRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("caretap_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_PROFILES = "profiles"
    }

    // Save full list
    fun saveProfiles(profiles: List<Profile>) {
        val json = gson.toJson(profiles)
        prefs.edit().putString(KEY_PROFILES, json).apply()
    }

    // Load full list, sorted by usageCount descending
    fun loadProfiles(): List<Profile> {
        val json = prefs.getString(KEY_PROFILES, null) ?: return emptyList()
        val type = object : TypeToken<List<Profile>>() {}.type
        val profiles: List<Profile> = gson.fromJson(json, type)
        return profiles.sortedByDescending { it.usageCount }
    }

    // Add a new profile
    fun addProfile(profile: Profile) {
        val list = loadProfiles().toMutableList()
        list.add(profile)
        saveProfiles(list)
    }

    // Delete a profile by ID
    fun deleteProfile(id: String) {
        val list = loadProfiles().filter { it.id != id }
        saveProfiles(list)
    }

    // Increment usage count (called when user taps a profile)
    fun incrementUsage(id: String) {
        val list = loadProfiles().toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            val profile = list[index]
            list[index] = profile.copy(usageCount = profile.usageCount + 1)
            saveProfiles(list)
        }
    }

    // Update profile image URI
    fun updateProfileImage(id: String, imageUri: String) {
        val list = loadProfiles().toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = list[index].copy(imageUri = imageUri)
            saveProfiles(list)
        }
    }
}