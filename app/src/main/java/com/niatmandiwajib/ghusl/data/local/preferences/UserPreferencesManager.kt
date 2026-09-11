package com.niatmandiwajib.ghusl.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ghusl_preferences")

class UserPreferencesManager(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("selected_language")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode") // "light", "dark", "system"
    }

    val selectedLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: getDefaultLanguage()
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE_KEY] ?: "system"
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode
        }
    }

    private fun getDefaultLanguage(): String {
        val locale = java.util.Locale.getDefault().language
        return when (locale) {
            "in", "id" -> "id"
            "ms" -> "ms"
            "ur" -> "ur"
            else -> "en"
        }
    }
}
