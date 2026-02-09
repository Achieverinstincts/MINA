package com.sekyiemmanuel.mina.feature.onboarding.domain

interface OnboardingRepository {
    fun hasCompletedOnboarding(): Boolean

    fun setOnboardingCompleted(completed: Boolean)
}
