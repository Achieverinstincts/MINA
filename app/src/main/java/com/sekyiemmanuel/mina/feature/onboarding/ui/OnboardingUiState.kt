package com.sekyiemmanuel.mina.feature.onboarding.ui

data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val data: OnboardingData = OnboardingData(),
    val aiSliderValue: Float = AIAssistanceLevel.BALANCED.sliderValue,
    val isLoading: Boolean = false,
    val showCustomTimePicker: Boolean = false,
    val showReminderTimePicker: Boolean = false,
) {
    val progress: Float
        get() = currentStep.ordinal.toFloat() / (OnboardingStep.totalSteps - 1).toFloat()

    val showBackButton: Boolean
        get() = currentStep != OnboardingStep.WELCOME

    val showProgress: Boolean
        get() = currentStep != OnboardingStep.WELCOME

    val showBottomNavigation: Boolean
        get() = currentStep != OnboardingStep.WELCOME && currentStep != OnboardingStep.CREATE_ACCOUNT

    val canProceed: Boolean
        get() = when (currentStep) {
            OnboardingStep.WELCOME -> true
            OnboardingStep.WHY_JOURNAL -> data.motivation != null
            OnboardingStep.EXPERIENCE_LEVEL -> data.experienceLevel != null
            OnboardingStep.JOURNALING_GOAL -> data.frequency != null
            OnboardingStep.PREFERRED_TIME -> true
            OnboardingStep.TOPICS -> true
            OnboardingStep.AI_ASSISTANCE -> true
            OnboardingStep.PRIVACY_SECURITY -> true
            OnboardingStep.HEALTH_SYNC -> true
            OnboardingStep.NOTIFICATIONS -> true
            OnboardingStep.SETUP_SUMMARY -> true
            OnboardingStep.CREATE_ACCOUNT -> false
        }

    val nextButtonText: String
        get() = when (currentStep) {
            OnboardingStep.WELCOME -> "Get Started"
            OnboardingStep.SETUP_SUMMARY -> "Continue"
            OnboardingStep.CREATE_ACCOUNT -> ""
            else -> "Next"
        }
}
