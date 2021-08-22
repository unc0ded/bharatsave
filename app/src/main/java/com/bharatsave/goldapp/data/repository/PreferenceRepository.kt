package com.bharatsave.goldapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val authTokenKey = stringPreferencesKey("auth_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val phoneNumberKey = stringPreferencesKey("mobile_number")
    private val firebaseUidKey = stringPreferencesKey("user_id")

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

    suspend fun savePhone(phoneNumber: String) {
        dataStore.edit { preferences ->
            preferences[phoneNumberKey] = phoneNumber
        }
    }

    suspend fun saveUid(firebaseUid: String) {
        dataStore.edit { preferences ->
            preferences[firebaseUidKey] = firebaseUid
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getAuthToken(): String {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences -> preferences[authTokenKey] ?: "" }
            .first()
    }

    suspend fun getRefreshToken(): String {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences -> preferences[refreshTokenKey] ?: "" }
            .first()
    }

    suspend fun getPhoneNumber(): String {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences -> preferences[phoneNumberKey] ?: "" }
            .first()
    }

    suspend fun getFirebaseUid(): String {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences -> preferences[firebaseUidKey] ?: "" }
            .first()
    }
}