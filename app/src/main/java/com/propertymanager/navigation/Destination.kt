package com.propertymanager.navigation

import androidx.compose.material3.Text
import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object PhoneScreen : Destination
    @Serializable
    data class OtpScreen(val phoneNumber: String) : Destination
    @Serializable
    data object OnboardingFormScreen : Destination
    @Serializable
    data object HomeScreen : Destination

    // Role-based destinations
    @Serializable
    data object TenantScreen : Destination
    @Serializable
    data object LandlordScreen : Destination
    @Serializable
    data object StaffScreen : Destination
}
