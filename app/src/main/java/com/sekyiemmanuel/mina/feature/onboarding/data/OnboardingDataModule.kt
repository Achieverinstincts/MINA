package com.sekyiemmanuel.mina.feature.onboarding.data

import com.sekyiemmanuel.mina.feature.onboarding.domain.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingDataModule {
    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        repository: SharedPrefsOnboardingRepository,
    ): OnboardingRepository
}
