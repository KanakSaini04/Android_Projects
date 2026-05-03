package com.clustr.app.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clustr.app.VoiceRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LibraryUiState(
    val recordings: List<VoiceRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LibraryViewModel : ViewModel() {
    private val _state = MutableStateFlow(LibraryUiState())
    val state: StateFlow<LibraryUiState> = _state.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    fun loadRecordings(uid: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val snapshot = db.collection("recordings")
                    .whereEqualTo("uid", uid)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val records = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(VoiceRecord::class.java)?.copy(id = doc.id)
                }
                _state.update { it.copy(recordings = records, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteRecording(record: VoiceRecord) {
        viewModelScope.launch {
            try {
                db.collection("recordings").document(record.id).delete().await()
                _state.update { it.copy(recordings = it.recordings.filter { r -> r.id != record.id }) }
            } catch (e: Exception) {
                // Handle delete error
            }
        }
    }
}
