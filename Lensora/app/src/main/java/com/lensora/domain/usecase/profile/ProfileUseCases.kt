package com.lensora.domain.usecase.profile

import com.lensora.domain.model.UserProfile
import com.lensora.domain.repository.AuthRepository
import com.lensora.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(private val repo: ProfileRepository) {
    suspend operator fun invoke(): UserProfile = repo.getProfile()
}
class UpdateProfileUseCase @Inject constructor(private val repo: ProfileRepository) {
    suspend operator fun invoke(name: String): Result<Unit> = repo.updateProfile(name)
}
class DeleteAccountUseCase @Inject constructor(private val repo: ProfileRepository) {
    suspend operator fun invoke(): Result<Unit> = repo.deleteAccount()
}
class SendPasswordResetFromProfileUseCase @Inject constructor(private val repo: ProfileRepository) {
    suspend operator fun invoke(email: String): Result<Unit> = repo.sendPasswordReset(email)
}
class GetPresetsUseCase @Inject constructor(private val repo: ProfileRepository) {
    suspend operator fun invoke(): List<String> = repo.getPresets()
}
class SavePresetUseCase @Inject constructor(private val repo: ProfileRepository) {
    suspend operator fun invoke(name: String) = repo.savePreset(name)
}
class DeletePresetUseCase @Inject constructor(private val repo: ProfileRepository) {
    suspend operator fun invoke(name: String) = repo.deletePreset(name)
}
class SignOutUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.signOut()
}
