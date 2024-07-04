package com.example.mobprostuff.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        private val IS_LIST_VIEW = booleanPreferencesKey("is_list_view")
    }

    val layoutFlow: Flow<Boolean> = context.dataStore.data.map {
        preferences -> preferences[IS_LIST_VIEW] ?: true
    }

    suspend fun saveLayout(isListView: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LIST_VIEW] = isListView
        }
    }
}