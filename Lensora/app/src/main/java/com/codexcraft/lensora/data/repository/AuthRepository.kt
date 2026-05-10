package com.codexcraft.lensora.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codexcraft.lensora.core.util.Constants
import com.codexcraft.lensora.data.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lensora_prefs")

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val nameKey = stringPreferencesKey(Constants.PREFS_USER_NAME)
    private val emailKey = stringPreferencesKey(Constants.PREFS_USER_EMAIL)
    private val photoKey = stringPreferencesKey(Constants.PREFS_USER_PHOTO)
    private val authKey = booleanPreferencesKey(Constants.PREFS_IS_AUTHENTICATED)

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[nameKey] ?: "",
            email = prefs[emailKey] ?: "",
            photoUrl = prefs[photoKey] ?: "",
            isAuthenticated = prefs[authKey] ?: false
        )
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[nameKey] = profile.name
            prefs[emailKey] = profile.email
            prefs[photoKey] = profile.photoUrl
            prefs[authKey] = profile.isAuthenticated
        }
    }

    suspend fun signOut() {
        context.dataStore.edit { it.clear() }
    }
}