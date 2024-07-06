package com.example.mobprostuff.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mobprostuff.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userPreference")

class UserDataStore(private val context: Context) {
    companion object {
        private val USER_NAME = stringPreferencesKey("userName")
        private val USER_EMAIL = stringPreferencesKey("userEmail")
        private val USER_PHOTO = stringPreferencesKey("photoUrl")
    }

    val userFlow: Flow<User> = context.dataStore.data
        .map { preferences -> User(
            userName = preferences[USER_NAME] ?: "",
            userEmail = preferences[USER_EMAIL] ?: "",
            photoUrl = preferences[USER_PHOTO] ?: ""
        )
    }

    suspend fun saveData(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = user.userName
            preferences[USER_EMAIL] = user.userEmail
            preferences[USER_PHOTO] = user.photoUrl
        }
    }
}