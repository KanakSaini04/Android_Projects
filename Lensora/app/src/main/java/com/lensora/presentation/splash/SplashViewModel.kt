package com.lensora.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lensora.core.utils.GuestExpiryManager
import com.lensora.domain.repository.AuthRepository
import com.lensora.domain.repository.OnboardingRepository
import com.lensora.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val onboardingRepository: OnboardingRepository,
    private val guestExpiryManager: GuestExpiryManager
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val onboardingDone = onboardingRepository.isOnboardingCompleted()
            val isLoggedIn = authRepository.isAuthenticated()
            val isGuestExpired = guestExpiryManager.isGuestExpired()

            _startDestination.value = when {
                !onboardingDone -> Screen.Onboarding.route
                !isLoggedIn || isGuestExpired -> Screen.Auth.route
                else -> Screen.Camera.route
            }
        }
    }
}
