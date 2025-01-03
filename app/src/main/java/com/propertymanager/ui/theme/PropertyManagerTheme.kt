package com.propertymanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.propertymanager.domain.ui.UiPreferences
import com.propertymanager.domain.ui.model.AppTheme
import com.propertymanager.ui.theme.colorscheme.BaseColorScheme
import com.propertymanager.ui.theme.colorscheme.GreenAppleColorScheme
import com.propertymanager.ui.theme.colorscheme.LavenderColorScheme
import com.propertymanager.ui.theme.colorscheme.MidnightDuskColorScheme
import com.propertymanager.ui.theme.colorscheme.MonetColorScheme
import com.propertymanager.ui.theme.colorscheme.NordColorScheme
import com.propertymanager.ui.theme.colorscheme.StrawberryColorScheme
import com.propertymanager.ui.theme.colorscheme.PropertyManagerColorScheme
import com.propertymanager.ui.theme.colorscheme.TakoColorScheme
import com.propertymanager.ui.theme.colorscheme.TealTurqoiseColorScheme
import com.propertymanager.ui.theme.colorscheme.TidalWaveColorScheme
import com.propertymanager.ui.theme.colorscheme.YinYangColorScheme
import com.propertymanager.ui.theme.colorscheme.YotsubaColorScheme

@Composable
fun PropertyManagerTheme(
    appTheme: AppTheme? = null,
    amoled: Boolean? = null,
    content: @Composable () -> Unit,
    uiPreferences: UiPreferences
) {
    BasePropertyManagerTheme(
        appTheme = appTheme ?: uiPreferences.appTheme().get(),
        isAmoled = amoled ?: uiPreferences.themeDarkAmoled().get(),
        content = content,
    )
}

@Composable
fun PropertyManagerPreviewTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit,
) = BasePropertyManagerTheme(appTheme, isAmoled, content)

@Composable
private fun BasePropertyManagerTheme(
    appTheme: AppTheme,
    isAmoled: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = getThemeColorScheme(appTheme, isAmoled),
        content = content,
    )
}

@Composable
@ReadOnlyComposable
private fun getThemeColorScheme(
    appTheme: AppTheme,
    isAmoled: Boolean,
): ColorScheme {
    val colorScheme = if (appTheme == AppTheme.MONET) {
        MonetColorScheme(LocalContext.current)
    } else {
        colorSchemes.getOrDefault(appTheme, PropertyManagerColorScheme)
    }
    return colorScheme.getColorScheme(
        isSystemInDarkTheme(),
        isAmoled,
    )
}

private val colorSchemes: Map<AppTheme, BaseColorScheme> = mapOf(
    AppTheme.DEFAULT to PropertyManagerColorScheme,
    AppTheme.GREEN_APPLE to GreenAppleColorScheme,
    AppTheme.LAVENDER to LavenderColorScheme,
    AppTheme.MIDNIGHT_DUSK to MidnightDuskColorScheme,
    AppTheme.NORD to NordColorScheme,
    AppTheme.STRAWBERRY_DAIQUIRI to StrawberryColorScheme,
    AppTheme.TAKO to TakoColorScheme,
    AppTheme.TEALTURQUOISE to TealTurqoiseColorScheme,
    AppTheme.TIDAL_WAVE to TidalWaveColorScheme,
    AppTheme.YINYANG to YinYangColorScheme,
    AppTheme.YOTSUBA to YotsubaColorScheme,
)
