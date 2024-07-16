package com.penguinstudios.tradeguardian.di

import com.penguinstudios.tradeguardian.data.BinanceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteRepositoryModule {

    @Provides
    @Singleton
    fun provideMainService(retrofit: Retrofit): BinanceService {
        return retrofit.create(BinanceService::class.java)
    }
}