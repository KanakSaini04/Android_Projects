package com.lensora.domain.usecase.onboarding

import com.lensora.domain.repository.OnboardingRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(private val repo: OnboardingRepository) {
    suspend operator fun invoke() = repo.setOnboardingCompleted()
}
class IsOnboardingCompletedUseCase @Inject constructor(private val repo: OnboardingRepository) {
    suspend operator fun invoke() = repo.isOnboardingCompleted()
}
