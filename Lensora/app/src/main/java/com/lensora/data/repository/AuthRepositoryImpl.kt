package com.lensora.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.google.firebase.auth.FirebaseAuth
import com.lensora.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataStore: DataStore<Preferences>
) : AuthRepository {

    companion object {
        val GUEST_START_TIME = longPreferencesKey("guest_start_time")
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null && !user.isEmailVerified) {
                firebaseAuth.signOut()
                Result.failure(Exception("Please verify your email before signing in"))
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<Unit> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.sendEmailVerification()?.await()
            result.user?.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build()
            )?.await()
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<Unit> {
        return Result.failure(Exception("Use Google launcher in UI"))
    }

    override suspend fun continueAsGuest(): Result<Unit> {
        return try {
            firebaseAuth.signInAnonymously().await()
            dataStore.edit { it[GUEST_START_TIME] = System.currentTimeMillis() }
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

    override suspend fun signOut() = firebaseAuth.signOut()
    override fun isAuthenticated() = firebaseAuth.currentUser != null
    override fun isGuest() = firebaseAuth.currentUser?.isAnonymous == true
    override fun isGuestExpired(): Boolean = false
}
