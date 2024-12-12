package com.propertymanager.bottomnav

import com.propertymanager.R
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomScreens<T>(
    val name: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val route: T
) {

    @Serializable
    data object Home : BottomScreens<Dest.MaintenanceListScreen>(
        name = "Home",
        unselectedIcon = R.drawable.home_filled,
        selectedIcon = R.drawable.home_unfilled,
        route = Dest.MaintenanceListScreen
    )

    @Serializable
    data object Settings : BottomScreens<Dest.TenantSettingsScreen>(
        name = "Settings",
        unselectedIcon = R.drawable.profile_filled,
        selectedIcon = R.drawable.profile_unfilled,
        route = Dest.TenantSettingsScreen
    )

}

/*
@Serializable
enum class BottomScreens(val name: String, val selectedIcon: Int, val unselectedIcon: Int, val route: Dest) {

    Home(
        name = "Home",
        selectedIcon = R.drawable.home_filled,
        unselectedIcon = R.drawable.home_unfilled,
        route = Dest.MaintenanceListScreen
    ),

    Settings(
        name = "Settings",
        selectedIcon = R.drawable.profile_filled,
        unselectedIcon = R.drawable.profile_unfilled,
        route = Dest.TenantSettingsScreen
    );
}*/
