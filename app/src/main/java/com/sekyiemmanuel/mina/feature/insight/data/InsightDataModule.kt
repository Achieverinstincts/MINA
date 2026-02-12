package com.sekyiemmanuel.mina.feature.insight.data

import com.sekyiemmanuel.mina.feature.insight.domain.InsightRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InsightDataModule {

    @Binds
    @Singleton
    abstract fun bindInsightRepository(
        repository: InMemoryInsightRepository,
    ): InsightRepository
}
