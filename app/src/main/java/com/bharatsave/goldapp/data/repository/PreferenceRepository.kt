package com.bharatsave.goldapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val authTokenKey = stringPreferencesKey("auth_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[authTokenKey] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[refreshTokenKey] = token
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getAuthToken(): Flow<String> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences -> preferences[authTokenKey] ?: "" }
    }

    fun getRefreshToken(): Flow<String> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences -> preferences[refreshTokenKey] ?: "" }
    }
}