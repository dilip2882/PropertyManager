package com.propertymanager.bottomnav.tenant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import propertymanager.i18n.MR

sealed class TenantBottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    data object Home : TenantBottomNavItem(
        route = "tenant_home",
        icon = Icons.Default.Home,
        label = "Home",
    )

    data object Profile : TenantBottomNavItem(
        route = "tenant_settings",
        icon = Icons.Default.Person,
        label = "Profile",
    )

    companion object {
        fun getAllItems() = listOf(Home, Profile)
    }
}
