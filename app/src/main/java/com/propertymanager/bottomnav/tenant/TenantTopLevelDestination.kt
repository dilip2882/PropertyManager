package com.propertymanager.bottomnav.tenant

import androidx.compose.ui.graphics.vector.ImageVector
import com.propertymanager.navigation.Dest
import propertymanager.i18n.MR
import propertymanager.presentation.components.PropertyManagerIcons
import kotlin.reflect.KClass

/**
 * Type for the top level destinations in the application. Contains metadata about the destination
 * that is used in the top app bar and common navigation UI.
 *
 * @param selectedIcon The icon to be displayed in the navigation UI when this destination is
 * selected.
 * @param unselectedIcon The icon to be displayed in the navigation UI when this destination is
 * not selected.
 * @param iconTextId Text that to be displayed in the navigation UI.
 * @param titleTextId Text that is displayed on the top app bar.
 * @param route The route to use when navigating to this destination.
 * @param baseRoute The highest ancestor of this destination. Defaults to [route], meaning that
 * there is a single destination in that section of the app (no nested destinations).
 */
enum class TenantTopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: String,
    val titleTextId: String,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    HOME(
        selectedIcon = PropertyManagerIcons.Home,
        unselectedIcon = PropertyManagerIcons.HomeBorder,
        iconTextId = MR.strings.app_name.toString(),
        titleTextId = MR.strings.app_name.toString(),
        route = Dest.MaintenanceListScreen::class,
        baseRoute = Dest.TenantScreen::class,
    ),
    SETTINGS(
        selectedIcon = PropertyManagerIcons.Settings,
        unselectedIcon = PropertyManagerIcons.SettingsBorder,
        iconTextId = MR.strings.app_name.toString(),
        titleTextId = MR.strings.app_name.toString(),
        route = Dest.TenantSettingsScreen::class,
        baseRoute = Dest.TenantScreen::class,
    ),
}
