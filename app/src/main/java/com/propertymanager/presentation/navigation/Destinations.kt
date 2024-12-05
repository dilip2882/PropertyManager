package com.propertymanager.presentation.navigation

sealed class Destinations(val route: String) {
    object SplashScreen : Destinations("splash_screen")
    object PhoneScreen : Destinations("phone_screen")
    object OtpScreen : Destinations("otp_screen")
    object OnboardingFormScreen : Destinations("onboarding_form_screen")
    object HomeScreen : Destinations("home_screen")
    object SettingsScreen : Destinations("settings_screen")
    object ProfileScreen : Destinations("profile_screen")
}