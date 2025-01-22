package com.propertymanager.domain.usecase.user

import com.propertymanager.domain.repository.UserRepository
import javax.inject.Inject

class UpdateSelectedPropertyUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, propertyId: String?) {
        repository.updateSelectedProperty(userId, propertyId)
    }
} 
