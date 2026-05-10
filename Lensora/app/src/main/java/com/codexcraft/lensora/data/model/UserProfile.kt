package com.codexcraft.lensora.data.model

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val isAuthenticated: Boolean = false
)