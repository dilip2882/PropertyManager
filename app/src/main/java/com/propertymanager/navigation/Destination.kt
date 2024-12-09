package com.propertymanager.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object PhoneScreen : Destination
    @Serializable
    data class OtpScreen(val phoneNumber: String) : Destination
}
