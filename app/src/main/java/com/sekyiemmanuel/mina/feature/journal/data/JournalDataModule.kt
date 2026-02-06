package com.sekyiemmanuel.mina.feature.journal.data

import com.sekyiemmanuel.mina.feature.journal.domain.JournalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class JournalDataModule {
    @Binds
    @Singleton
    abstract fun bindJournalRepository(
        repository: InMemoryJournalRepository,
    ): JournalRepository
}

