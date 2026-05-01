package com.codexcraft.caretap.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.codexcraft.caretap.data.model.Profile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Single source of truth for all Profile data.
 *
 * Storage engine : SharedPreferences  (no DB needed — list stays small)
 * Serialization  : GSON
 * Sorting        : By usageCount descending — most-used contacts appear first.
 *
 * All functions are synchronous / lightweight — safe to call from a ViewModel
 * on the main thread for this data size (< 100 profiles).
 */
class StorageRepository(context: Context) {

    companion object {
        private const val PREFS_NAME = "caretap_prefs"
        private const val KEY_PROFILES = "profiles"
    }

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    // ─────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────

    /**
     * Returns all saved profiles sorted by usageCount (highest first).
     * Returns an empty list if nothing has been saved yet.
     */
    fun getProfiles(): List<Profile> {
        val json = prefs.getString(KEY_PROFILES, null) ?: return emptyList()
        val type = object : TypeToken<List<Profile>>() {}.type
        val profiles: List<Profile> = gson.fromJson(json, type) ?: emptyList()
        return profiles.sortedByDescending { it.usageCount }   // ← smart sort
    }

    // ─────────────────────────────────────────────
    // WRITE
    // ─────────────────────────────────────────────

    /**
     * Replaces the entire profile list in storage.
     * Call this after any add / update / delete operation.
     */
    fun saveProfiles(profiles: List<Profile>) {
        val json = gson.toJson(profiles)
        prefs.edit().putString(KEY_PROFILES, json).apply()
    }

    // ─────────────────────────────────────────────
    // CONVENIENCE HELPERS
    // ─────────────────────────────────────────────

    /** Adds a brand-new profile. */
    fun addProfile(profile: Profile) {
        val updated = getProfiles().toMutableList().also { it.add(profile) }
        saveProfiles(updated)
    }

    /** Replaces the profile with the same id. No-op if not found. */
    fun updateProfile(updated: Profile) {
        val list = getProfiles().toMutableList()
        val index = list.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            list[index] = updated
            saveProfiles(list)
        }
    }

    /** Removes a profile by id. No-op if not found. */
    fun deleteProfile(profileId: String) {
        val updated = getProfiles().filterNot { it.id == profileId }
        saveProfiles(updated)
    }

    /**
     * Increments usageCount by 1 for the given profile id.
     * Call this every time the user taps a profile tile so the
     * grid automatically re-orders by most-used over time.
     */
    fun incrementUsage(profileId: String) {
        val list = getProfiles().toMutableList()
        val index = list.indexOfFirst { it.id == profileId }
        if (index != -1) {
            list[index] = list[index].copy(usageCount = list[index].usageCount + 1)
            saveProfiles(list)
        }
    }
}