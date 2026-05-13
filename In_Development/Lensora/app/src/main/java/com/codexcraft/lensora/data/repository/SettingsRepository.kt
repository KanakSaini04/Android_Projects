package com.codexcraft.lensora.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "lensora_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mirrorSyncKey = booleanPreferencesKey("mirror_sync")
    private val findFeatureKey = booleanPreferencesKey("find_feature")

    val isMirrorSyncEnabled: Flow<Boolean> = context.settingsStore.data
        .map { it[mirrorSyncKey] ?: false }

    val isFindEnabled: Flow<Boolean> = context.settingsStore.data
        .map { it[findFeatureKey] ?: false }

    suspend fun setMirrorSync(enabled: Boolean) {
        context.settingsStore.edit { it[mirrorSyncKey] = enabled }
    }

    suspend fun setFind(enabled: Boolean) {
        context.settingsStore.edit { it[findFeatureKey] = enabled }
    }
}