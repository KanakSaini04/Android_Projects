package com.codexcraft.lensora.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.lensora.core.util.Constants
import com.codexcraft.lensora.data.model.UserProfile
import com.codexcraft.lensora.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.userProfile.collect { profile ->
                if (profile.isAuthenticated && _authState.value is AuthState.Idle) {
                    _authState.value = AuthState.Success(profile)
                }
            }
        }
    }

    // ── Email / Username login ──────────────────────────────────────────────
    fun signInWithEmail(name: String, email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            if (name.isBlank()) {
                _authState.value = AuthState.Error("Name cannot be empty")
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _authState.value = AuthState.Error("Enter a valid email address")
                return@launch
            }

            val profile = UserProfile(
                name = name.trim(),
                email = email.trim(),
                photoUrl = "",
                isAuthenticated = true
            )
            authRepository.saveUserProfile(profile)
            _authState.value = AuthState.Success(profile)
        }
    }

    // ── Google Sign-In (bottom sheet picker) ────────────────────────────────
    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credentialManager = CredentialManager.create(activityContext)

                val signInWithGoogleOption = GetSignInWithGoogleOption
                    .Builder(Constants.GOOGLE_WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()

                val response = credentialManager.getCredential(
                    request = request,
                    context = activityContext
                )

                val credential = response.credential
                android.util.Log.d("LENSORA_AUTH", "Credential type: ${credential.type}")
                android.util.Log.d("LENSORA_AUTH", "Credential data: ${credential.data}")

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    android.util.Log.d("LENSORA_AUTH", "Display name: ${googleCredential.displayName}")
                    android.util.Log.d("LENSORA_AUTH", "Email: ${googleCredential.id}")

                    val profile = UserProfile(
                        name = googleCredential.displayName ?: "Lensora User",
                        email = googleCredential.id,
                        photoUrl = googleCredential.profilePictureUri?.toString() ?: "",
                        isAuthenticated = true
                    )
                    authRepository.saveUserProfile(profile)
                    _authState.value = AuthState.Success(profile)

                } else {
                    android.util.Log.e("LENSORA_AUTH", "Wrong credential type: ${credential.type}")
                    _authState.value = AuthState.Error("Unexpected credential type: ${credential.type}")
                }

            } catch (e: NoCredentialException) {
                android.util.Log.e("LENSORA_AUTH", "NoCredentialException: ${e.message}")
                _authState.value = AuthState.Error(
                    "No Google account found. Add one in Settings → Accounts."
                )
            } catch (e: GetCredentialCancellationException) {
                android.util.Log.d("LENSORA_AUTH", "User cancelled sign-in")
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                android.util.Log.e("LENSORA_AUTH", "Exception: ${e::class.simpleName}: ${e.message}")
                _authState.value = AuthState.Error("${e::class.simpleName}: ${e.message}")
            }
        }
    }
}