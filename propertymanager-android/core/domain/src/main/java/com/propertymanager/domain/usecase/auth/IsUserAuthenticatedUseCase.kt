package com.propertymanager.domain.usecase.auth

import com.propertymanager.domain.repository.AuthRepository
import javax.inject.Inject

class IsUserAuthenticatedUseCase @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke() = repository.isUserAuthenticatedInFirebase()
}
