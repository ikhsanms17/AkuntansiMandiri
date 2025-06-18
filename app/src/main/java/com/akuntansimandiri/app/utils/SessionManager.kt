package com.akuntansimandiri.app.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

object SessionManager {
    private val KEY_USER_ID = intPreferencesKey("user_id")
    private val KEY_USER_NAME = stringPreferencesKey("user_name")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")


    suspend fun saveSession(context: Context, id: Int, nama: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = id
            preferences[KEY_USER_NAME] = nama
            preferences[KEY_USER_EMAIL] = email
            preferences[IS_LOGGED_IN] = true

        }
    }

    suspend fun getSession(context: Context): Triple<Int, String, String>? {
        return context.dataStore.data.map { preferences ->
            val id = preferences[KEY_USER_ID] ?: -1
            val name = preferences[KEY_USER_NAME] ?: ""
            val email = preferences[KEY_USER_EMAIL] ?: ""
            if (id != -1 && name.isNotEmpty() && email.isNotEmpty()) Triple(id, name, email) else null
        }.first()
    }

    suspend fun clearSession(context: Context) {
        context.dataStore.edit {
            it.clear()
        }

    }

    suspend fun isLoggedIn(context: Context): Boolean {
        val value = context.dataStore.data.map { it[IS_LOGGED_IN] == true }.first()
        return value
    }

    suspend fun setLoggedIn(context: Context, status: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = status
        }
    }

    suspend fun saveUserName(context: Context, name: String) {
        context.dataStore.edit { it[KEY_USER_NAME] = name }
    }

    suspend fun saveUserEmail(context: Context, email: String) {
        context.dataStore.edit { it[KEY_USER_EMAIL] = email }
    }


    suspend fun getUserName(context: Context): String? {
        return context.dataStore.data.map { it[KEY_USER_NAME] }.first()
    }

    suspend fun getUserEmail(context: Context): String? {
        return context.dataStore.data.map { it[KEY_USER_EMAIL] }.first()
    }

    suspend fun getUserId(context: Context): Int? {
        return context.dataStore.data.map { it[KEY_USER_ID] }.first()
    }
}
