package com.codexcraft.lensora.domain.usecase

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.codexcraft.lensora.core.util.Constants
import com.codexcraft.lensora.data.model.UserProfile
import com.codexcraft.lensora.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(activityContext: Context): Result<UserProfile> {
        return try {
            val credentialManager = CredentialManager.create(activityContext)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(Constants.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val profile = UserProfile(
                    name = googleCredential.displayName ?: "Lensora User",
                    email = googleCredential.id,
                    photoUrl = googleCredential.profilePictureUri?.toString() ?: "",
                    isAuthenticated = true
                )
                authRepository.saveUserProfile(profile)
                Result.success(profile)
            } else {
                Result.failure(Exception("Invalid credential type"))
            }
        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}