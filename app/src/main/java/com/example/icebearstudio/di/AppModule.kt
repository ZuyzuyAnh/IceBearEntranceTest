package com.example.icebearstudio.di

import android.app.Application
import androidx.room.Room
import com.example.icebearstudio.data.Repository
import com.example.icebearstudio.data.RepositoryImpl
import com.example.icebearstudio.data.local.AppDatabase
import com.example.icebearstudio.data.local.TaskDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideApDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "appDB"
        ).build()
    }
    @Singleton
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDAO {
        return database.taskDao()
    }
    @Singleton
    @Provides
    fun provideRepository(
        taskDAO: TaskDAO
    ):Repository = RepositoryImpl(taskDAO)
}