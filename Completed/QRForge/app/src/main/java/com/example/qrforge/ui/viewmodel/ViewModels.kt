package com.example.qrforge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrforge.data.local.ScanHistoryDao
import com.example.qrforge.data.local.ScanHistoryEntity
import com.example.qrforge.data.preferences.AppSettings
import com.example.qrforge.data.preferences.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

// ─── Scan ────────────────────────────────────────────────
@HiltViewModel
class ScanViewModel @Inject constructor(
    private val dao: ScanHistoryDao,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun saveToHistory(rawValue: String, type: String, format: String) = viewModelScope.launch {
        val history = dao.getLastScanned()
        if (history?.rawValue == rawValue) return@launch  // duplicate — skip
        dao.insert(
            ScanHistoryEntity(
                rawValue    = rawValue,
                type        = type,
                format      = format,
                timestamp   = LocalDateTime.now().toString(),
                isGenerated = false
            )
        )
    }
}

// ─── Create ──────────────────────────────────────────────
@HiltViewModel
class CreateViewModel @Inject constructor(
    private val dao: ScanHistoryDao,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun saveGenerated(content: String, type: String) = viewModelScope.launch {
        dao.insert(
            ScanHistoryEntity(
                rawValue    = content,
                type        = type,
                format      = "QR_CODE",
                timestamp   = LocalDateTime.now().toString(),
                isGenerated = true
            )
        )
    }
}

// ─── History ─────────────────────────────────────────────
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val dao: ScanHistoryDao
) : ViewModel() {

    fun filteredHistory(query: String, type: String) =
        dao.getFilteredHistory(query, type)

    fun deleteItem(item: ScanHistoryEntity) = viewModelScope.launch {
        dao.delete(item)
    }

    fun toggleFavorite(item: ScanHistoryEntity) = viewModelScope.launch {
        dao.setFavorite(item.id, !item.isFavorite)
    }

    fun clearAll() = viewModelScope.launch {
        dao.clearAll()
    }

    fun exportHistory(
        history: List<ScanHistoryEntity>,
        onExport: (String) -> Unit
    ) {
        val csv = buildString {
            appendLine("ID,Type,Format,Value,Timestamp,Favorite,Generated")
            history.forEach { item ->
                appendLine(
                    "${item.id}," +
                            "${item.type}," +
                            "${item.format}," +
                            "\"${item.rawValue.replace("\"", "\"\"")}\"," +
                            "${item.timestamp}," +
                            "${item.isFavorite}," +
                            "${item.isGenerated}"
                )
            }
        }
        onExport(csv)
    }
}

// ─── Settings ────────────────────────────────────────────
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setDarkTheme(v: Boolean)      = viewModelScope.launch { repo.setDarkTheme(v) }
    fun setAutoOpenUrls(v: Boolean)   = viewModelScope.launch { repo.setAutoOpenUrls(v) }
    fun setBeepOnScan(v: Boolean)     = viewModelScope.launch { repo.setBeepOnScan(v) }
    fun setBiometricLock(v: Boolean)  = viewModelScope.launch { repo.setBiometricLock(v) }
    fun setQrSize(v: Int)             = viewModelScope.launch { repo.setQrSize(v) }
    fun setVibration(v: Boolean)      = viewModelScope.launch { repo.setVibration(v) }
    fun setOnboardingDone(v: Boolean) = viewModelScope.launch { repo.setOnboardingDone(v) }
    fun setAutoCopy(v: Boolean)       = viewModelScope.launch { repo.setAutoCopy(v) }
}