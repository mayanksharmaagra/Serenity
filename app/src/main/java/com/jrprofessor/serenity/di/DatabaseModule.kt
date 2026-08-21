package com.jrprofessor.serenity.di

import android.content.Context
import com.jrprofessor.serenity.data.local.MoodDao
import com.jrprofessor.serenity.data.local.SerenityDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSerenityDatabase(
        @ApplicationContext context: Context
    ): SerenityDatabase {
        return SerenityDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideMoodDao(database: SerenityDatabase): MoodDao {
        return database.moodDao()
    }
}
