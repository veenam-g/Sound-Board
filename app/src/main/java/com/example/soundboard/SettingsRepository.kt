package com.example.soundboard

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val THEME_KEY = intPreferencesKey("theme")
        val LAST_SOUNDBANK_KEY = stringPreferencesKey("last_soundbank")
    }

    // Get the theme preference
    val theme: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: 0 // Default to light theme (0)
        }

    // Save the theme preference
    suspend fun saveTheme(theme: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    // Get the last selected soundbank
    val lastSoundbank: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SOUNDBANK_KEY] ?: ""
        }

    // Save the last selected soundbank
    suspend fun saveLastSoundbank(soundbankName: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SOUNDBANK_KEY] = soundbankName
        }
    }
}