package com.sweetshop.customer.data.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sweet_shop_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveUserInfo(userId: Long, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
            prefs[USER_NAME_KEY] = name
            prefs[USER_EMAIL_KEY] = email
        }
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN_KEY]
        }.first()
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]
        }.first()
    }

    suspend fun getUserId(): Long? {
        return context.dataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }.first()
    }

    suspend fun getUserName(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[USER_NAME_KEY]
        }.first()
    }

    suspend fun getUserEmail(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[USER_EMAIL_KEY]
        }.first()
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
