package com.nymp.phselgy.feature_load

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PreferencesManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("loadingHeart")
        val LINK = stringPreferencesKey("heart1")
        val FIRST = booleanPreferencesKey("heart2")
        val FIRST_VIEW = booleanPreferencesKey("heart3")
    }

    fun getLink(): Flow<String> {
        return context.dataStore.data.map {preferences ->
            preferences[LINK] ?: ""
        }
    }

    suspend fun setLink(newLink: String) {
        context.dataStore.edit { settings ->
            settings[LINK] = newLink
        }
    }

    fun getFirstLaunch(): Flow<Boolean> {
        return context.dataStore.data.map {preferences ->
            preferences[FIRST] ?: true
        }
    }

    suspend fun setNotFirstLaunch() {
        context.dataStore.edit { settings ->
            settings[FIRST] = false
        }
    }

    fun getFirstView(): Flow<Boolean> {
        return context.dataStore.data.map {preferences ->
            preferences[FIRST_VIEW] ?: true
        }
    }

    suspend fun setNotFirstView() {
        context.dataStore.edit { settings ->
            settings[FIRST_VIEW] = false
        }
    }
}


