package com.sweetshop.admin.data.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "admin_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN_KEY]
    }

    val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL_KEY]
    }

    fun getAccessTokenSync(): String? {
        return runBlocking {
            context.dataStore.data.first()[ACCESS_TOKEN_KEY]
        }
    }

    fun getRefreshTokenSync(): String? {
        return runBlocking {
            context.dataStore.data.first()[REFRESH_TOKEN_KEY]
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveUserInfo(name: String, email: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
            prefs[USER_EMAIL_KEY] = email
            prefs[USER_ROLE_KEY] = role
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    fun isLoggedIn(): Boolean {
        return getAccessTokenSync() != null
    }
}
