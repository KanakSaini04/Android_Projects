package com.example.qrforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "qrforge_settings")

data class AppSettings(
    val isDarkTheme: Boolean = false,
    val autoOpenUrls: Boolean = false,
    val beepOnScan: Boolean = true,
    val biometricLock: Boolean = false,
    val qrSize: Int = 512,
    val vibrationOnScan: Boolean = true,
    val onboardingDone: Boolean = false,
    val autoCopyOnScan: Boolean = false,
)

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DARK_THEME      = booleanPreferencesKey("dark_theme")
        val AUTO_OPEN_URLS  = booleanPreferencesKey("auto_open_urls")
        val BEEP_ON_SCAN    = booleanPreferencesKey("beep_on_scan")
        val BIOMETRIC_LOCK  = booleanPreferencesKey("biometric_lock")
        val QR_SIZE         = intPreferencesKey("qr_size")
        val VIBRATION       = booleanPreferencesKey("vibration")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val AUTO_COPY       = booleanPreferencesKey("auto_copy")
    }

    val settings: Flow<AppSettings> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            AppSettings(
                isDarkTheme     = prefs[Keys.DARK_THEME]      ?: false,
                autoOpenUrls    = prefs[Keys.AUTO_OPEN_URLS]  ?: false,
                beepOnScan      = prefs[Keys.BEEP_ON_SCAN]    ?: true,
                biometricLock   = prefs[Keys.BIOMETRIC_LOCK]  ?: false,
                qrSize          = prefs[Keys.QR_SIZE]         ?: 512,
                vibrationOnScan = prefs[Keys.VIBRATION]       ?: true,
                onboardingDone  = prefs[Keys.ONBOARDING_DONE] ?: false,
                autoCopyOnScan  = prefs[Keys.AUTO_COPY]       ?: false,
            )
        }

    suspend fun setDarkTheme(v: Boolean)     = context.dataStore.edit { it[Keys.DARK_THEME]      = v }
    suspend fun setAutoOpenUrls(v: Boolean)  = context.dataStore.edit { it[Keys.AUTO_OPEN_URLS]  = v }
    suspend fun setBeepOnScan(v: Boolean)    = context.dataStore.edit { it[Keys.BEEP_ON_SCAN]    = v }
    suspend fun setBiometricLock(v: Boolean) = context.dataStore.edit { it[Keys.BIOMETRIC_LOCK]  = v }
    suspend fun setQrSize(v: Int)            = context.dataStore.edit { it[Keys.QR_SIZE]         = v }
    suspend fun setVibration(v: Boolean)     = context.dataStore.edit { it[Keys.VIBRATION]       = v }
    suspend fun setOnboardingDone(v: Boolean)= context.dataStore.edit { it[Keys.ONBOARDING_DONE] = v }
    suspend fun setAutoCopy(v: Boolean)      = context.dataStore.edit { it[Keys.AUTO_COPY]       = v }
}