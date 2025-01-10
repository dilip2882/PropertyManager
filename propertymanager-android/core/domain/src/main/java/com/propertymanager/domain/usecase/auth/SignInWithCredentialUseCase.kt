package com.propertymanager.domain.usecase.auth

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithCredentialUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(otp: String): Flow<Response<String>> {
        return authRepository.signInWithCredential(otp)
    }
}
