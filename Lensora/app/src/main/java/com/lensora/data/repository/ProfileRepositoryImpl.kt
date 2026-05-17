package com.lensora.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lensora.domain.model.UserProfile
import com.lensora.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val dataStore: DataStore<Preferences>
) : ProfileRepository {

    companion object {
        val GUEST_START_TIME = longPreferencesKey("guest_start_time")
        val PRESETS_KEY = stringPreferencesKey("saved_presets")
    }

    override suspend fun getProfile(): UserProfile {
        val user = firebaseAuth.currentUser
        val isGuest = user?.isAnonymous == true
        val guestDaysLeft = if (isGuest) {
            val prefs = dataStore.data.first()
            val startTime = prefs[GUEST_START_TIME] ?: System.currentTimeMillis().also {
                dataStore.edit { p -> p[GUEST_START_TIME] = it }
            }
            val elapsed = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - startTime)
            maxOf(0, 7 - elapsed.toInt())
        } else 0

        return UserProfile(
            name = user?.displayName ?: "Guest User",
            email = user?.email ?: "",
            photoUrl = user?.photoUrl?.toString(),
            isGuest = isGuest,
            guestDaysLeft = guestDaysLeft,
            totalPhotos = 0,
            bestShots = 0
        )
    }

    override suspend fun updateProfile(name: String): Result<Unit> {
        return try {
            val request = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            firebaseAuth.currentUser?.updateProfile(request)?.await()
            firebaseAuth.currentUser?.uid?.let { uid ->
                firestore.collection("users").document(uid)
                    .set(mapOf("name" to name), SetOptions.merge()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.uid?.let { uid ->
                firestore.collection("users").document(uid).delete().await()
            }
            firebaseAuth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPresets(): List<String> {
        val prefs = dataStore.data.first()
        val raw = prefs[PRESETS_KEY] ?: return emptyList()
        return raw.split(",").filter { it.isNotBlank() }
    }

    override suspend fun savePreset(name: String) {
        dataStore.edit { prefs ->
            val current = prefs[PRESETS_KEY] ?: ""
            val list = current.split(",").filter { it.isNotBlank() }.toMutableList()
            if (!list.contains(name)) { list.add(name); prefs[PRESETS_KEY] = list.joinToString(",") }
        }
    }

    override suspend fun deletePreset(name: String) {
        dataStore.edit { prefs ->
            val current = prefs[PRESETS_KEY] ?: ""
            val list = current.split(",").filter { it.isNotBlank() && it != name }
            prefs[PRESETS_KEY] = list.joinToString(",")
        }
    }
}
