package com.propertymanager.common.preferences.temp

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val BIOMETRIC_AUTH = booleanPreferencesKey("is_biometric_auth_enabled")
    val LANGUAGE_KEY = stringPreferencesKey("language")

}
