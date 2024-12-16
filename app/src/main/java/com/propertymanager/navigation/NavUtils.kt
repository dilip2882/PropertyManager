package com.propertymanager.navigation

import kotlinx.serialization.Serializable

sealed class SubGraph {

    @Serializable
    data object Home : SubGraph()

    @Serializable
    data object Auth : SubGraph()

    @Serializable
    data object Tenant : SubGraph()

    @Serializable
    data object Landlord : SubGraph()

    @Serializable
    data object Staff : SubGraph()

}

sealed interface Dest {

    @Serializable
    data object PhoneScreen : Dest

    @Serializable
    data class OtpScreen(val phoneNumber: String) : Dest

    @Serializable
    data object OnboardingFormScreen : Dest

    @Serializable
    data object HomeScreen : Dest

    // Role-based destinations
    @Serializable
    data object TenantScreen : Dest

    @Serializable
    data object LandlordScreen : Dest

    @Serializable
    data object StaffScreen : Dest

    // Tenant screens

    @Serializable
    data object MaintenanceRequestScreen : Dest

    @Serializable
    data object MaintenanceListScreen : Dest

    @Serializable
    data object TenantSettingsScreen : Dest

}

