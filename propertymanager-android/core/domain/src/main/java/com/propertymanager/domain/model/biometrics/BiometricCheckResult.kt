package com.propertymanager.domain.model.biometrics

sealed interface BiometricCheckResult {
    data object Available : BiometricCheckResult
    data object NoHardware : BiometricCheckResult
    data object HardwareUnavailable : BiometricCheckResult
    data object NoneEnrolled : BiometricCheckResult
}
