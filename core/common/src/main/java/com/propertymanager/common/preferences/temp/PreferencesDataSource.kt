package com.propertymanager.common.preferences.temp

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.propertymanager.common.preferences.Preference
import com.propertymanager.common.preferences.PreferenceStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore("settings")

class PreferencesDataSource(private val context: Context) : PreferenceStore {
    val dynamicColor: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.DYNAMIC_COLOR] ?: false
    }
    val darkMode: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.DARK_MODE] ?: false
    }

    val biometricAuth: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.BIOMETRIC_AUTH] ?: false
    }

    override fun getString(key: String, defaultValue: String): Preference<String> {
        return object : Preference<String> {
            override fun key(): String = key
            override fun get(): String = runBlocking {
                context.dataStore.data.first()[stringPreferencesKey(key)] ?: defaultValue
            }
            override fun set(value: String) {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences[stringPreferencesKey(key)] = value
                    }
                }
            }
            override fun isSet(): Boolean = runBlocking {
                context.dataStore.data.first().contains(stringPreferencesKey(key))
            }
            override fun delete() {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences.remove(stringPreferencesKey(key))
                    }
                }
            }
            override fun defaultValue(): String = defaultValue
            override fun changes(): Flow<String> = context.dataStore.data.map {
                it[stringPreferencesKey(key)] ?: defaultValue
            }
            override fun stateIn(scope: kotlinx.coroutines.CoroutineScope) = 
                changes().stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, get())
        }
    }

    override fun getLong(key: String, defaultValue: Long): Preference<Long> {
        return object : Preference<Long> {
            override fun key(): String = key
            override fun get(): Long = runBlocking {
                context.dataStore.data.first()[longPreferencesKey(key)] ?: defaultValue
            }
            override fun set(value: Long) {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences[longPreferencesKey(key)] = value
                    }
                }
            }
            override fun isSet(): Boolean = runBlocking {
                context.dataStore.data.first().contains(longPreferencesKey(key))
            }
            override fun delete() {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences.remove(longPreferencesKey(key))
                    }
                }
            }
            override fun defaultValue(): Long = defaultValue
            override fun changes(): Flow<Long> = context.dataStore.data.map {
                it[longPreferencesKey(key)] ?: defaultValue
            }
            override fun stateIn(scope: kotlinx.coroutines.CoroutineScope) = 
                changes().stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, get())
        }
    }

    override fun getInt(key: String, defaultValue: Int): Preference<Int> {
        return object : Preference<Int> {
            override fun key(): String = key
            override fun get(): Int = runBlocking {
                context.dataStore.data.first()[intPreferencesKey(key)] ?: defaultValue
            }
            override fun set(value: Int) {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences[intPreferencesKey(key)] = value
                    }
                }
            }
            override fun isSet(): Boolean = runBlocking {
                context.dataStore.data.first().contains(intPreferencesKey(key))
            }
            override fun delete() {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences.remove(intPreferencesKey(key))
                    }
                }
            }
            override fun defaultValue(): Int = defaultValue
            override fun changes(): Flow<Int> = context.dataStore.data.map {
                it[intPreferencesKey(key)] ?: defaultValue
            }
            override fun stateIn(scope: kotlinx.coroutines.CoroutineScope) = 
                changes().stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, get())
        }
    }

    override fun getFloat(key: String, defaultValue: Float): Preference<Float> {
        return object : Preference<Float> {
            override fun key(): String = key
            override fun get(): Float = runBlocking {
                context.dataStore.data.first()[floatPreferencesKey(key)] ?: defaultValue
            }
            override fun set(value: Float) {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences[floatPreferencesKey(key)] = value
                    }
                }
            }
            override fun isSet(): Boolean = runBlocking {
                context.dataStore.data.first().contains(floatPreferencesKey(key))
            }
            override fun delete() {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences.remove(floatPreferencesKey(key))
                    }
                }
            }
            override fun defaultValue(): Float = defaultValue
            override fun changes(): Flow<Float> = context.dataStore.data.map {
                it[floatPreferencesKey(key)] ?: defaultValue
            }
            override fun stateIn(scope: kotlinx.coroutines.CoroutineScope) = 
                changes().stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, get())
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Preference<Boolean> {
        return object : Preference<Boolean> {
            override fun key(): String = key
            override fun get(): Boolean = runBlocking {
                context.dataStore.data.first()[booleanPreferencesKey(key)] ?: defaultValue
            }
            override fun set(value: Boolean) {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences[booleanPreferencesKey(key)] = value
                    }
                }
            }
            override fun isSet(): Boolean = runBlocking {
                context.dataStore.data.first().contains(booleanPreferencesKey(key))
            }
            override fun delete() {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences.remove(booleanPreferencesKey(key))
                    }
                }
            }
            override fun defaultValue(): Boolean = defaultValue
            override fun changes(): Flow<Boolean> = context.dataStore.data.map {
                it[booleanPreferencesKey(key)] ?: defaultValue
            }
            override fun stateIn(scope: kotlinx.coroutines.CoroutineScope) = 
                changes().stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, get())
        }
    }

    override fun getStringSet(key: String, defaultValue: Set<String>): Preference<Set<String>> {
        return object : Preference<Set<String>> {
            override fun key(): String = key
            override fun get(): Set<String> = runBlocking {
                context.dataStore.data.first()[stringSetPreferencesKey(key)] ?: defaultValue
            }
            override fun set(value: Set<String>) {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences[stringSetPreferencesKey(key)] = value
                    }
                }
            }
            override fun isSet(): Boolean = runBlocking {
                context.dataStore.data.first().contains(stringSetPreferencesKey(key))
            }
            override fun delete() {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences.remove(stringSetPreferencesKey(key))
                    }
                }
            }
            override fun defaultValue(): Set<String> = defaultValue
            override fun changes(): Flow<Set<String>> = context.dataStore.data.map {
                it[stringSetPreferencesKey(key)] ?: defaultValue
            }
            override fun stateIn(scope: kotlinx.coroutines.CoroutineScope) = 
                changes().stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, get())
        }
    }

    override fun <T> getObject(
        key: String,
        defaultValue: T,
        serializer: (T) -> String,
        deserializer: (String) -> T
    ): Preference<T> {
        return object : Preference<T> {
            override fun key(): String = key
            override fun get(): T = runBlocking {
                context.dataStore.data.first()[stringPreferencesKey(key)]?.let(deserializer) ?: defaultValue
            }
            override fun set(value: T) {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences[stringPreferencesKey(key)] = serializer(value)
                    }
                }
            }
            override fun isSet(): Boolean = runBlocking {
                context.dataStore.data.first().contains(stringPreferencesKey(key))
            }
            override fun delete() {
                runBlocking {
                    context.dataStore.edit { preferences ->
                        preferences.remove(stringPreferencesKey(key))
                    }
                }
            }
            override fun defaultValue(): T = defaultValue
            override fun changes(): Flow<T> = context.dataStore.data.map {
                it[stringPreferencesKey(key)]?.let(deserializer) ?: defaultValue
            }
            override fun stateIn(scope: kotlinx.coroutines.CoroutineScope) = 
                changes().stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, get())
        }
    }

    override fun getAll(): Map<String, *> = runBlocking {
        context.dataStore.data.first().asMap().mapKeys { it.key.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    suspend fun setBiometricAuth(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_AUTH] = enabled
        }
    }
}
