package com.vidflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vidflow.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val store = SettingsDataStore(app)

    val theme = store.theme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "dark")
    val appLock = store.appLock.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val biometric = store.biometric.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val defaultQuality = store.defaultQuality.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "720p")

    fun setTheme(value: String) = viewModelScope.launch { store.setTheme(value) }
    fun setAppLock(value: Boolean) = viewModelScope.launch { store.setAppLock(value) }
    fun setBiometric(value: Boolean) = viewModelScope.launch { store.setBiometric(value) }
    fun setDefaultQuality(value: String) = viewModelScope.launch { store.setDefaultQuality(value) }
}