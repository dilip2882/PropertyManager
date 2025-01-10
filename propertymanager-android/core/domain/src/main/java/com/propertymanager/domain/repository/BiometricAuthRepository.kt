package com.propertymanager.domain.repository

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.propertymanager.domain.model.biometrics.AuthenticationResult
import com.propertymanager.domain.model.biometrics.BiometricCheckResult
import kotlinx.coroutines.flow.Flow

interface BiometricAuthRepository {
    fun authenticateWithBiometrics(activity: FragmentActivity): Flow<AuthenticationResult>
    fun checkBiometricsAvailability(context: Context): Flow<BiometricCheckResult>
}
