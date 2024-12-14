package com.propertymanager.domain.usecase.auth

import com.propertymanager.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = authRepository.getFirebaseAuthState()
}
