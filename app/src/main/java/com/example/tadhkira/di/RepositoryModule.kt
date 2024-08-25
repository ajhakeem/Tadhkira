package com.example.tadhkira.di

import com.example.tadhkira.data.adhan.AdhanRepository
import com.example.tadhkira.data.adhan.AdhanService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAdhanRepository(adhanService: AdhanService) =
        AdhanRepository(adhanService)
}