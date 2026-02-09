package com.sekyiemmanuel.mina.app.navigation

import androidx.lifecycle.ViewModel
import com.sekyiemmanuel.mina.feature.onboarding.domain.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AppNavViewModel @Inject constructor(
    onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private val _startDestination = MutableStateFlow(
        if (onboardingRepository.hasCompletedOnboarding()) {
            AppDestinations.JOURNAL
        } else {
            AppDestinations.ONBOARDING
        },
    )
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()
}
