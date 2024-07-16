package com.penguinstudios.tradeguardian.di

import android.content.Context
import androidx.room.Room
import com.penguinstudios.tradeguardian.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "trade-guardian-db")
            .fallbackToDestructiveMigration()
            .build()
    }
}
