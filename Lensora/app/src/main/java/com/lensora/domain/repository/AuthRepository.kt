package com.lensora.domain.repository

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, name: String): Result<Unit>
    suspend fun signInWithGoogle(): Result<Unit>
    suspend fun continueAsGuest(): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signOut()
    fun isAuthenticated(): Boolean
    fun isGuest(): Boolean
    fun isGuestExpired(): Boolean
}
