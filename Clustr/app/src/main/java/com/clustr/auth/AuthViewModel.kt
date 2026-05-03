package com.clustr.app.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clustr.app.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.clustr.app.R
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: User? = null
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AuthRepository()

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(app.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(app, gso)
    }

    init {
        viewModelScope.launch {
            repo.authState.collect { firebaseUser ->
                if (firebaseUser != null) {
                    val profile = repo.getUserProfile(firebaseUser.uid)
                    _state.value = AuthUiState(isAuthenticated = true, user = profile)
                } else {
                    _state.value = AuthUiState(isAuthenticated = false)
                }
            }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repo.signUpWithEmail(username, email, password)) {
                is AuthResult.Success -> { /* authState flow handles state update */ }
                is AuthResult.Error   -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> {}
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repo.signInWithEmail(email, password)) {
                is AuthResult.Success -> { /* authState flow handles state update */ }
                is AuthResult.Error   -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> {}
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repo.signInWithGoogle(account)) {
                is AuthResult.Success -> { /* authState flow handles state update */ }
                is AuthResult.Error   -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> {}
            }
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
        repo.signOut()
    }

    fun clearError() = _state.update { it.copy(errorMessage = null) }

    fun updateMicEnabled(uid: String, enabled: Boolean) {
        viewModelScope.launch {
            repo.updateUserProfile(uid, mapOf("micEnabled" to enabled))
            _state.update { it.copy(user = it.user?.copy(micEnabled = enabled)) }
        }
    }

    fun updateBiometricEnabled(uid: String, enabled: Boolean) {
        viewModelScope.launch {
            repo.updateUserProfile(uid, mapOf("biometricEnabled" to enabled))
            _state.update { it.copy(user = it.user?.copy(biometricEnabled = enabled)) }
        }
    }
}