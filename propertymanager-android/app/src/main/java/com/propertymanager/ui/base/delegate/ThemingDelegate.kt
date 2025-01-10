package com.propertymanager.ui.base.delegate

import android.app.Activity
import com.propertymanager.R
import com.propertymanager.domain.ui.UiPreferences
import com.propertymanager.domain.ui.model.AppTheme
import javax.inject.Inject

interface ThemingDelegate {
    fun applyAppTheme(activity: Activity)

    companion object {
        fun getThemeResIds(appTheme: AppTheme, isAmoled: Boolean): List<Int> {
            return buildList(2) {
                add(themeResources.getOrDefault(appTheme, R.style.Theme_PropertyManager))
                if (isAmoled) add(R.style.ThemeOverlay_PropertyManager_Amoled)
            }
        }
    }
}

class ThemingDelegateImpl @Inject constructor(
    private val uiPreferences: UiPreferences
) : ThemingDelegate {

    override fun applyAppTheme(activity: Activity) {
        ThemingDelegate.getThemeResIds(uiPreferences.appTheme().get(), uiPreferences.themeDarkAmoled().get())
            .forEach(activity::setTheme)
    }
}

private val themeResources: Map<AppTheme, Int> = mapOf(
    AppTheme.MONET to R.style.Theme_PropertyManager_Monet,
    AppTheme.GREEN_APPLE to R.style.Theme_PropertyManager_GreenApple,
    AppTheme.LAVENDER to R.style.Theme_PropertyManager_Lavender,
    AppTheme.MIDNIGHT_DUSK to R.style.Theme_PropertyManager_MidnightDusk,
    AppTheme.NORD to R.style.Theme_PropertyManager_Nord,
    AppTheme.STRAWBERRY_DAIQUIRI to R.style.Theme_PropertyManager_StrawberryDaiquiri,
    AppTheme.TAKO to R.style.Theme_PropertyManager_Tako,
    AppTheme.TEALTURQUOISE to R.style.Theme_PropertyManager_TealTurquoise,
    AppTheme.YINYANG to R.style.Theme_PropertyManager_YinYang,
    AppTheme.YOTSUBA to R.style.Theme_PropertyManager_Yotsuba,
    AppTheme.TIDAL_WAVE to R.style.Theme_PropertyManager_TidalWave,
)
