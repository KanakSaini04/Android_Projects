package com.codexcraft.fileflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FileFlowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize crash reporting, analytics, etc.
    }
}
