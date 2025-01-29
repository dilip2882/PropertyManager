package propertymanager.feature.auth.presentation.mvi

import android.app.Activity
import propertymanager.presentation.mvi.MVIContract

interface AuthContract : MVIContract<AuthContract.AuthState, AuthContract.AuthEffect, AuthContract.AuthEvent> {

    sealed class AuthEvent {
        data class SubmitPhoneNumber(val phoneNumber: String, val activity: Activity) : AuthEvent()
        data class SubmitOtp(val otp: String) : AuthEvent()
        data class ResendOtp(val phoneNumber: String, val activity: Activity) : AuthEvent()
        object ObserveAuthState : AuthEvent()
        object SignOut : AuthEvent()
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Authenticated(val isAuthenticated: Boolean) : AuthState()
        data class OtpSent(val message: String) : AuthState()
        data class Error(val errorMessage: String) : AuthState()
    }

    sealed class AuthEffect {
        object NavigateToHome : AuthEffect()
        object NavigateToOtpScreen : AuthEffect()
        object NNavigateToPhoneScreen: AuthEffect()
        data class ShowToast(val message: String) : AuthEffect()
    }
}
