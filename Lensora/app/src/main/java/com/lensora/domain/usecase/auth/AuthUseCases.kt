package com.lensora.domain.usecase.auth

import com.lensora.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.signIn(email, password)
}
class SignUpUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, name: String) = repo.signUp(email, password, name)
}
class SignInWithGoogleUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.signInWithGoogle()
}
class ContinueAsGuestUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.continueAsGuest()
}
class SendPasswordResetUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String) = repo.sendPasswordReset(email)
}
