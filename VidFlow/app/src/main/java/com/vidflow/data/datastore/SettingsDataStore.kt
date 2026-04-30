package com.vidflow.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val THEME           = stringPreferencesKey("theme")
    val APP_LOCK        = booleanPreferencesKey("app_lock")
    val BIOMETRIC       = booleanPreferencesKey("biometric")
    val DEFAULT_QUALITY = stringPreferencesKey("default_quality")
}

class SettingsDataStore(private val context: Context) {

    val theme: Flow<String> = context.dataStore.data
        .map { it[SettingsKeys.THEME] ?: "dark" }

    val appLock: Flow<Boolean> = context.dataStore.data
        .map { it[SettingsKeys.APP_LOCK] ?: false }

    val biometric: Flow<Boolean> = context.dataStore.data
        .map { it[SettingsKeys.BIOMETRIC] ?: true }

    val defaultQuality: Flow<String> = context.dataStore.data
        .map { it[SettingsKeys.DEFAULT_QUALITY] ?: "720p" }

    suspend fun setTheme(value: String) {
        context.dataStore.edit { it[SettingsKeys.THEME] = value }
    }

    suspend fun setAppLock(value: Boolean) {
        context.dataStore.edit { it[SettingsKeys.APP_LOCK] = value }
    }

    suspend fun setBiometric(value: Boolean) {
        context.dataStore.edit { it[SettingsKeys.BIOMETRIC] = value }
    }

    suspend fun setDefaultQuality(value: String) {
        context.dataStore.edit { it[SettingsKeys.DEFAULT_QUALITY] = value }
    }
}