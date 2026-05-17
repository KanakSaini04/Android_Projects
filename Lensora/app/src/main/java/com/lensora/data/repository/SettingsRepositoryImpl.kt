package com.lensora.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.lensora.domain.repository.SettingsRepository
import com.lensora.presentation.settings.SettingsUiState
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        val AUTO_SCENE = booleanPreferencesKey("auto_scene")
        val HDR_DEFAULT = booleanPreferencesKey("hdr_default")
        val AUTO_CAPTURE = booleanPreferencesKey("auto_capture")
        val HAPTIC = booleanPreferencesKey("haptic")
        val POSE = booleanPreferencesKey("pose")
        val COMPOSITION = booleanPreferencesKey("composition")
        val DSLR_BLUR = booleanPreferencesKey("dslr_blur")
        val SAVE_ORIGINAL = booleanPreferencesKey("save_original")
        val PHOTO_QUALITY = stringPreferencesKey("photo_quality")
    }

    override suspend fun getSettings(): SettingsUiState {
        val prefs = dataStore.data.first()
        return SettingsUiState(
            autoSceneDetection = prefs[AUTO_SCENE] ?: true,
            hdrDefault = prefs[HDR_DEFAULT] ?: true,
            autoCapture = prefs[AUTO_CAPTURE] ?: false,
            hapticFeedback = prefs[HAPTIC] ?: true,
            poseGuidance = prefs[POSE] ?: false,
            compositionGuide = prefs[COMPOSITION] ?: true,
            dslrBlur = prefs[DSLR_BLUR] ?: true,
            saveOriginal = prefs[SAVE_ORIGINAL] ?: false,
            photoQuality = prefs[PHOTO_QUALITY] ?: "High"
        )
    }

    override suspend fun saveSettings(settings: SettingsUiState) {
        dataStore.edit { prefs ->
            prefs[AUTO_SCENE] = settings.autoSceneDetection
            prefs[HDR_DEFAULT] = settings.hdrDefault
            prefs[AUTO_CAPTURE] = settings.autoCapture
            prefs[HAPTIC] = settings.hapticFeedback
            prefs[POSE] = settings.poseGuidance
            prefs[COMPOSITION] = settings.compositionGuide
            prefs[DSLR_BLUR] = settings.dslrBlur
            prefs[SAVE_ORIGINAL] = settings.saveOriginal
            prefs[PHOTO_QUALITY] = settings.photoQuality
        }
    }
}
