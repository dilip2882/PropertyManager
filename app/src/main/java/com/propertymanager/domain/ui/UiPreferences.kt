package com.propertymanager.domain.ui

import com.propertymanager.common.preferences.PreferenceStore
import com.propertymanager.common.preferences.getEnum
import com.propertymanager.common.system.DeviceUtil
import com.propertymanager.domain.ui.model.AppTheme
import com.propertymanager.ui.util.system.isDynamicColorAvailable
import com.propertymanager.domain.ui.model.ThemeMode
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiPreferences @Inject constructor(
    val preferenceStore: PreferenceStore
) {
    fun themeMode() = preferenceStore.getEnum("pref_theme_mode_key", ThemeMode.SYSTEM)

    fun appTheme() = preferenceStore.getEnum(
        "pref_app_theme",
        if (DeviceUtil.isDynamicColorAvailable) {
            AppTheme.MONET
        } else {
            AppTheme.DEFAULT
        },
    )

    fun themeDarkAmoled() = preferenceStore.getBoolean("pref_theme_dark_amoled_key", false)

    fun relativeTime() = preferenceStore.getBoolean("relative_time_v2", true)

    fun dateFormat() = preferenceStore.getString("app_date_format", "")
}
