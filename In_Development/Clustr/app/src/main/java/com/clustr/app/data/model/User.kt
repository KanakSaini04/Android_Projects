package com.clustr.app.data.model

import com.google.firebase.Timestamp

/**
 * Represents the user's profile and application settings.
 * biometricEnabled: Tracks if the user has opted into biometric security.
 * micEnabled: Tracks the preferred state of the live acoustic input.
 */
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val biometricEnabled: Boolean = false,
    val micEnabled: Boolean = true,
    val createdAt: Timestamp? = null
)
