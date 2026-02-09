package com.sekyiemmanuel.mina.feature.onboarding.data

import android.content.Context
import com.sekyiemmanuel.mina.feature.onboarding.domain.OnboardingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsOnboardingRepository @Inject constructor(
    @ApplicationContext context: Context,
) : OnboardingRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun hasCompletedOnboarding(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    override fun setOnboardingCompleted(completed: Boolean) {
        prefs
            .edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, completed)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "mina_onboarding_prefs"
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
