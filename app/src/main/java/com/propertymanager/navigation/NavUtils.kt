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

    /* ----------------- * Auth * ----------------- */
    @Serializable
    data object PhoneScreen : Dest
    @Serializable
    data class OtpScreen(val phoneNumber: String) : Dest
    @Serializable
    data object OnboardingFormScreen : Dest
    @Serializable
    data object HomeScreen : Dest

    @Serializable
    data object PropertyManagerApp : Dest

    /* ----------------- * Role-based destinations * ----------------- */
    @Serializable
    data object TenantScreen : Dest
    @Serializable
    data object LandlordScreen : Dest
    @Serializable
    data object StaffScreen : Dest

    @Serializable
    data object ProfileScreen: Dest
    @Serializable
    data object EditProfileScreen: Dest

    /* ----------------- * Tenant screens * ----------------- */
    @Serializable
    data object MaintenanceListScreen : Dest
    @Serializable
    data class MaintenanceDetailsScreen(
        val requestId: String
    ) : Dest
    @Serializable
    data object MaintenanceCategoriesScreen : Dest
    @Serializable
    data class MaintenanceRequestScreen(
        val category: String,
        val subcategory: String
    ) : Dest
    @Serializable
    data object TenantSettingsScreen : Dest

    /* ----------------- * Staff screens * ----------------- */
    @Serializable
    data object StaffHomeScreen : Dest
    @Serializable
    data object StaffSettingsScreen : Dest

    // Category
    @Serializable
    data object CategoryManagerScreen: Dest

    // Location
    @Serializable
    data object LocationScreen: Dest
    @Serializable
    data object LocationManagerScreen: Dest

    // Property
    @Serializable
    data object PropertyManagerScreen: Dest
    @Serializable
    data object AddPropertyScreen: Dest
    @Serializable
    data class EditPropertyScreen(val propertyId: String): Dest
    @Serializable
    data object SelectCountryScreen: Dest
    @Serializable
    data object SelectStateScreen: Dest
    @Serializable
    data object SelectCityScreen: Dest
}

