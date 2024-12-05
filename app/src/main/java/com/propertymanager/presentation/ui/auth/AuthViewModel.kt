package com.propertymanager.presentation.ui.auth

import android.app.Activity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.domain.usecase.AuthenticationUseCases
import com.propertymanager.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthenticationUseCases
) : ViewModel() {

    private val _isUserAuthenticated = mutableStateOf(false)
    val isUserAuthenticated: State<Boolean> get() = _isUserAuthenticated

    // Use Flow for authentication state
    private val _authState = mutableStateOf<Boolean>(false)
    val authState: State<Boolean> = _authState

    private val _otpVerificationState = mutableStateOf<Response<String>>(Response.Loading)
    val otpVerificationState: State<Response<String>> = _otpVerificationState

    // OTP resend state
    private val _otpResendState = mutableStateOf<Response<String>>(Response.Loading)
    val otpResendState: State<Response<String>> = _otpResendState

    private val _signOutState = mutableStateOf<Response<Boolean>>(Response.Loading)
    val signOutState: State<Response<Boolean>> = _signOutState

    init {
        observeAuthState()
    }

    // Observing Firebase authentication state
    private fun observeAuthState() {
        viewModelScope.launch {
            authUseCases.firebaseAuthStateUseCase().collect { isAuthenticated ->
                _authState.value = isAuthenticated
            }
        }
    }

    fun createUserWithPhone(phone: String, activity: Activity) {
        viewModelScope.launch {
            authUseCases.createUserWithPhoneUseCase(phone, activity).collect { result ->
                _otpVerificationState.value = result
            }
        }
    }

    fun signInWithCredential(otp: String) {
        viewModelScope.launch {
            authUseCases.signInWithCredentialUseCase(otp).collect { result ->
                _otpVerificationState.value = result
            }
        }
    }

    fun resendOtp(phone: String, activity: Activity) {
        viewModelScope.launch {
            authUseCases.resendOtpUseCase(phone, activity).collect { result ->
                _otpResendState.value = result
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authUseCases.signOutUseCase().collect { result ->
                _signOutState.value = result
            }
        }
    }
}
