package com.codexcraft.caretap.data.model

import java.util.UUID

/**
 * Represents a person/contact tile shown on the Home grid.
 *
 * @param id          Unique identifier — auto-generated UUID, never changes.
 * @param name        Display name shown under the tile image.
 * @param phone       Phone number used for Call / WhatsApp actions.
 * @param imageUri    URI string of the photo (camera capture or gallery pick). Null = placeholder.
 * @param usageCount  Incremented every time the user taps this tile. Used for smart sorting.
 */
data class Profile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val phone: String = "",
    val imageUri: String? = null,
    val usageCount: Int = 0
)