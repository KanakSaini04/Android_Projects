package com.codexcraft.caretap.utils

import android.Manifest
import android.os.Build

object PermissionManager {

    // All permissions the app needs
    fun requiredPermissions(): Array<String> {
        val permissions = mutableListOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+
            permissions.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        }
        return permissions.toTypedArray()
    }

    fun cameraPermission() = Manifest.permission.CAMERA
}