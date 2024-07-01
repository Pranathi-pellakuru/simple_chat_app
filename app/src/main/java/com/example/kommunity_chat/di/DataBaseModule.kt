package com.example.kommunity_chat.di

import android.content.Context
import androidx.room.Room
import com.example.kommunity_chat.data.dao.MessageDAO
import com.example.kommunity_chat.data.dao.UserDAO
import com.example.kommunity_chat.data.Database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): UserDAO {
        return appDatabase.userDao()
    }

    @Provides
    fun provideMessageDao(appDatabase: AppDatabase): MessageDAO {
        return appDatabase.messageDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "my_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
