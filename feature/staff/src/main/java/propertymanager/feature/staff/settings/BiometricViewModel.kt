package propertymanager.feature.staff.settings

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.domain.model.biometrics.AuthenticationResult
import com.propertymanager.domain.model.biometrics.BiometricAuthState
import com.propertymanager.domain.model.biometrics.BiometricCheckResult
import com.propertymanager.domain.usecase.biometrics.BiometricAuthUseCase
import com.propertymanager.domain.usecase.biometrics.BiometricAvailabilityUseCase
import com.propertymanager.domain.usecase.biometrics.GetBiometricAuthUseCase
import com.propertymanager.domain.usecase.biometrics.SetBiometricAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel @Inject constructor(
    private val useCase: BiometricAuthUseCase,
    private val getBiometricAuthUseCase: GetBiometricAuthUseCase,
    private val setBiometricAuthUseCase: SetBiometricAuthUseCase,
    private val biometricAvailabilityUseCase: BiometricAvailabilityUseCase,
) : ViewModel() {

    private val _biometricAvailability: MutableStateFlow<BiometricCheckResult> =
        MutableStateFlow(BiometricCheckResult.NoneEnrolled)
    val biometricAvailability: StateFlow<BiometricCheckResult> get() = _biometricAvailability

    private val _hasAuthenticated = MutableStateFlow(false)
    val hasAuthenticated: StateFlow<Boolean> get() = _hasAuthenticated

    private val _authResult = MutableStateFlow<AuthenticationResult>(AuthenticationResult.Uninitialized)
    val authResult: StateFlow<AuthenticationResult> get() = _authResult

    private val _biometricAuthState: MutableStateFlow<BiometricAuthState> = MutableStateFlow(BiometricAuthState.LOADING)
    val biometricAuthState: StateFlow<BiometricAuthState> = _biometricAuthState

    init {

        getBiometricAuthUseCase.execute().map { isEnabled ->
            _biometricAuthState.value = if (isEnabled) {
                BiometricAuthState.ENABLED
            } else {
                _hasAuthenticated.value = true
                BiometricAuthState.DISABLED
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            biometricAvailabilityUseCase.execute().collect {
                _biometricAvailability.value = it
            }
        }
    }

    fun setBiometricAuth(enabled: Boolean) {
        viewModelScope.launch {
            setBiometricAuthUseCase.execute(enabled)
        }
    }

    fun authenticate(activity: FragmentActivity) {
        viewModelScope.launch {
            useCase.execute(activity).collect { result ->
                _authResult.value = result
            }
        }
    }

    fun handleBiometricAuth(
        biometricAuthResult: AuthenticationResult,
        context: Context,
    ) {
        when (biometricAuthResult) {
            is AuthenticationResult.Error -> {
                if (biometricAuthResult.errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(context, biometricAuthResult.errorMessage, Toast.LENGTH_SHORT).show()
                }
                (context as? Activity)?.finish()
            }
            AuthenticationResult.Failure -> {
                (context as? Activity)?.finish()
            }
            AuthenticationResult.Success -> {
                _hasAuthenticated.value = true
                return
            }
            AuthenticationResult.Uninitialized -> Unit
        }
    }
}
