package com.sekyiemmanuel.mina.feature.gallery.data

import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GalleryDataModule {

    @Binds
    @Singleton
    abstract fun bindGalleryRepository(
        repository: InMemoryGalleryRepository,
    ): GalleryRepository
}
