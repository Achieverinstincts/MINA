package com.sekyiemmanuel.mina.feature.inbox.data

import com.sekyiemmanuel.mina.feature.inbox.domain.InboxRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InboxDataModule {
    @Binds
    @Singleton
    abstract fun bindInboxRepository(
        repository: InMemoryInboxRepository,
    ): InboxRepository
}
