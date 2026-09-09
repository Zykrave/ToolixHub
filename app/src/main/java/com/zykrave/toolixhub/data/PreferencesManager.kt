package com.zykrave.toolixhub.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "toolixhub_preferences")

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val KEY_FAVORITES = stringSetPreferencesKey("favorites_set")
        private val KEY_RECENTS = stringPreferencesKey("recents_list")
    }

    val themeModeFlow: Flow<AppThemeMode> = context.dataStore.data.map { preferences ->
        val modeStr = preferences[KEY_THEME_MODE] ?: AppThemeMode.DARK.name
        try {
            AppThemeMode.valueOf(modeStr)
        } catch (e: Exception) {
            AppThemeMode.DARK
        }
    }

    val dynamicColorFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_DYNAMIC_COLOR] ?: true
    }

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[KEY_FAVORITES] ?: setOf(
            "calc_standard",
            "text_counter",
            "timer_countdown",
            "sensor_compass",
            "qr_generator"
        )
    }

    val recentsFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val raw = preferences[KEY_RECENTS] ?: ""
        if (raw.isBlank()) {
            emptyList()
        } else {
            raw.split(",").filter { it.isNotBlank() }
        }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode.name
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun toggleFavorite(toolId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[KEY_FAVORITES]?.toMutableSet() ?: mutableSetOf(
                "calc_standard",
                "text_counter",
                "timer_countdown",
                "sensor_compass",
                "qr_generator"
            )
            if (current.contains(toolId)) {
                current.remove(toolId)
            } else {
                current.add(toolId)
            }
            preferences[KEY_FAVORITES] = current
        }
    }

    suspend fun addRecent(toolId: String) {
        context.dataStore.edit { preferences ->
            val raw = preferences[KEY_RECENTS] ?: ""
            val current = if (raw.isBlank()) mutableListOf() else raw.split(",").filter { it.isNotBlank() }.toMutableList()
            current.remove(toolId)
            current.add(0, toolId)
            val trimmed = current.take(5)
            preferences[KEY_RECENTS] = trimmed.joinToString(",")
        }
    }

    suspend fun recordRecent(toolId: String) = addRecent(toolId)

    suspend fun clearRecents() {
        context.dataStore.edit { preferences ->
            preferences[KEY_RECENTS] = ""
        }
    }

    suspend fun resetFavoritesAndHistory() {
        context.dataStore.edit { preferences ->
            preferences[KEY_FAVORITES] = emptySet()
            preferences[KEY_RECENTS] = ""
        }
    }
}
