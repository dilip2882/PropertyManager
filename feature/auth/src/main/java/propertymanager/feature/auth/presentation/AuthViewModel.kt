package propertymanager.feature.auth.presentation

import android.app.Activity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.propertymanager.common.preferences.AppPreferences
import com.propertymanager.common.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import propertymanager.feature.auth.domain.usecase.AuthenticationUseCases
import propertymanager.feature.auth.presentation.mvi.AuthContract
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthenticationUseCases,
    private val firebaseAuth: FirebaseAuth,
    private val appPreferences: AppPreferences
    ) : ViewModel(), AuthContract {

    private val mutableState = MutableStateFlow<AuthContract.AuthState>(AuthContract.AuthState.Idle)
    private val mutableEffect = MutableSharedFlow<AuthContract.AuthEffect>()

    override val state: StateFlow<AuthContract.AuthState> = mutableState
    override val effect: SharedFlow<AuthContract.AuthEffect> = mutableEffect.asSharedFlow()

    override fun event(event: AuthContract.AuthEvent) {
        when (event) {
            is AuthContract.AuthEvent.SubmitPhoneNumber -> submitPhoneNumber(event.phoneNumber, event.activity)
            is AuthContract.AuthEvent.SubmitOtp -> submitOtp(event.otp)
            is AuthContract.AuthEvent.ResendOtp -> resendOtp(event.phoneNumber, event.activity)
            AuthContract.AuthEvent.ObserveAuthState -> observeAuthState()
            AuthContract.AuthEvent.SignOut -> signOut()
        }
    }

    private fun submitPhoneNumber(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            mutableState.value = AuthContract.AuthState.Loading
            authUseCases.createUserWithPhoneUseCase(phoneNumber, activity).collect { result ->
                when (result) {
                    is Response.Success -> mutableEffect.emit(AuthContract.AuthEffect.NavigateToOtpScreen)
                    is Response.Error -> mutableState.value = AuthContract.AuthState.Error(result.message)
                    Response.Loading -> mutableState.value = AuthContract.AuthState.Loading
                }
            }
        }
    }

    private fun submitOtp(otp: String) {
        viewModelScope.launch {
            mutableState.value = AuthContract.AuthState.Loading
            authUseCases.signInWithCredentialUseCase(otp).collect { result ->
                when (result) {
                    is Response.Success -> mutableEffect.emit(AuthContract.AuthEffect.NavigateToHome)
                    is Response.Error -> {
                        // Update state to show error
                        mutableState.value = AuthContract.AuthState.Error(result.message)
                        mutableEffect.emit(AuthContract.AuthEffect.ShowToast(result.message))
                    }
                    Response.Loading -> mutableState.value = AuthContract.AuthState.Loading
                }
            }
        }
    }

    private fun resendOtp(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            authUseCases.resendOtpUseCase(phoneNumber, activity).collect { result ->
                when (result) {
                    is Response.Success -> mutableState.value = AuthContract.AuthState.OtpSent("OTP Sent Again!")
                    is Response.Error -> mutableState.value = AuthContract.AuthState.Error(result.message)
                    Response.Loading -> mutableState.value = AuthContract.AuthState.Loading
                }
            }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authUseCases.firebaseAuthStateUseCase().collect { isAuthenticated ->
                if (isAuthenticated) {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        appPreferences.saveAuthToken(currentUser.uid) // Store the token
                    }
                }
                mutableState.value = AuthContract.AuthState.Authenticated(isAuthenticated)
            }
        }
    }


    private fun signOut() {
        viewModelScope.launch {
            authUseCases.signOutUseCase().collect { result ->
                when (result) {
                    is Response.Loading -> mutableState.value = AuthContract.AuthState.Loading
                    is Response.Success -> mutableEffect.emit(AuthContract.AuthEffect.ShowToast("Signed Out Successfully"))
                    is Response.Error -> mutableState.value = AuthContract.AuthState.Error(result.message)
                }
            }
        }
    }
}
