package com.propertymanager.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreUtil @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun setData(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value.toString()
        }
    }

    suspend fun getData(key: String): Boolean? {
        val preferences = dataStore.data.first()
        val value = preferences[stringPreferencesKey(key)]
        return value?.toBoolean()
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
