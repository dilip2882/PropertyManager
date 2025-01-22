package com.propertymanager.domain.usecase.user

import com.propertymanager.domain.model.User
import com.propertymanager.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<User?> = repository.getCurrentUser()
} 
