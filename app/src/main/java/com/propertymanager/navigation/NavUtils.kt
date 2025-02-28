package com.propertymanager.navigation

import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.Role
import kotlinx.serialization.Serializable

sealed class SubGraph {
    @Serializable
    data object Auth : SubGraph()

    @Serializable
    data object Tenant : SubGraph()

    @Serializable
    data object Landlord : SubGraph()

    @Serializable
    data object Staff : SubGraph()

    fun fromRole(role: Role): Dest = when (role) {
        Role.TENANT -> Dest.TenantScreen
        Role.MANAGER -> Dest.StaffScreen
        Role.LANDLORD -> Dest.LandlordScreen
    }

}

sealed interface Dest {

    /* ----------------- * Auth * ----------------- */
    @Serializable
    data object PhoneScreen : Dest
    @Serializable
    data class OtpScreen(val phoneNumber: String) : Dest
    @Serializable
    data object OnboardingFormScreen : Dest

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
    data object TenantHomeScreen : Dest
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
    @Serializable
    data object SelectSocietyScreen: Dest
    @Serializable
    data class SelectFlatScreen(
        val parentId: Int,
        val buildingType: Property.Building
    ) : Dest
    @Serializable
    data class CityPropertiesScreen(val cityId: Int): Dest

    /* ----------------- * Staff screens * ----------------- */
    @Serializable
    data object StaffFlatScreen: Dest
    @Serializable
    data class StaffHomeScreen(val propertyId: String): Dest {
        fun createRoute(propertyId: String) = "staff/home/$propertyId"
    }
    @Serializable
    data object StaffSettingsScreen : Dest

    // Category
    @Serializable
    data object CategoryManagerScreen: Dest

    // Location
    @Serializable
    data object CountryManagerScreen : Dest

    @Serializable
    data class StateManagerScreen(val countryId: Int) : Dest

    @Serializable
    data class CityManagerScreen(val stateId: Int) : Dest

    @Serializable
    data class LocationScreen(val cityId: Int) : Dest
    @Serializable
    data class LocationManagerScreen(val cityId: Int): Dest

    // Property
    @Serializable
    data object PropertyApproveScreen: Dest
}
