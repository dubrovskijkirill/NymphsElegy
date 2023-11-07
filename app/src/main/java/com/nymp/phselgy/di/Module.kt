package com.nymp.phselgy.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context : Context): SharedPreferences {
        return context.getSharedPreferences("Shared", Context.MODE_PRIVATE)
    }
}