package com.nymp.phselgy.di

import android.content.Context
import android.content.SharedPreferences
import com.nymp.phselgy.feature_load.NympthLoadingRepository
import com.nymp.phselgy.feature_load.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("Shared", Context.MODE_PRIVATE)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object LoaderModule {

    @Provides
    @Singleton
    fun providePrefMan(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideLoadingRepository(
        preferencesManager: PreferencesManager, @ApplicationContext context: Context
    ): NympthLoadingRepository {
        return NympthLoadingRepository(preferencesManager, context)
    }
}