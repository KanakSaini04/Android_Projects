package com.codexcraft.fileflow.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("fileflow_prefs")

object PrefKeys {
    val LAST_ROOT_URI = stringPreferencesKey("last_root_uri")
}

class UserPreferences(private val context: Context) {
    val lastRootUri: Flow<String?> = context.dataStore.data.map { it[PrefKeys.LAST_ROOT_URI] }

    suspend fun setLastRootUri(uri: String) {
        context.dataStore.edit { it[PrefKeys.LAST_ROOT_URI] = uri }
    }
}
