package com.propertymanager.domain.usecase.auth

import com.propertymanager.domain.repository.AuthRepository
import com.propertymanager.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<Response<Boolean>> {
        return authRepository.firebaseSignOut()
    }
}