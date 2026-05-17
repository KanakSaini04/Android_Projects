package com.lensora.domain.model

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val isGuest: Boolean = false,
    val guestDaysLeft: Int = 7,
    val totalPhotos: Int = 0,
    val bestShots: Int = 0
)
