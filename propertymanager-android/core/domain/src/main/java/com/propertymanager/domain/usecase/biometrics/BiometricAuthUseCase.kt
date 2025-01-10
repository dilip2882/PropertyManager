package com.propertymanager.domain.usecase.biometrics

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.propertymanager.domain.model.biometrics.AuthenticationResult
import com.propertymanager.domain.repository.BiometricAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BiometricAuthUseCase @Inject constructor (private val repository: BiometricAuthRepository) {
    fun execute(context: FragmentActivity): Flow<AuthenticationResult> {
        return repository.authenticateWithBiometrics(context)
    }
}
