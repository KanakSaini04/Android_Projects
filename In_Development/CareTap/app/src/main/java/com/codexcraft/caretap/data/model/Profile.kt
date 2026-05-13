package com.codexcraft.caretap.data.model

data class Profile(
    val id: String,
    val name: String,
    val phone: String,
    val imageUri: String = "",
    val usageCount: Int = 0
)