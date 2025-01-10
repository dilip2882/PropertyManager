package com.propertymanager.domain.usecase.biometrics

import com.propertymanager.domain.repository.PreferencesRepository
import javax.inject.Inject

class SetBiometricAuthUseCase  @Inject constructor(private val repository: PreferencesRepository) {
    suspend fun execute(enabled:Boolean){
        repository.setBiometricAuth(enabled)
    }
}
