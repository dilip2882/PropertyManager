package com.propertymanager.domain.usecase.biometrics

import com.propertymanager.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBiometricAuthUseCase @Inject constructor(private val repository: PreferencesRepository){
    fun execute():Flow<Boolean> = repository.biometricAuth

}
