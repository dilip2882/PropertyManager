package com.propertymanager.domain.usecase.property

import com.propertymanager.domain.model.Property
import com.propertymanager.domain.repository.PropertyRepository
import javax.inject.Inject

class GetPropertyByIdUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(propertyId: String): Property? {
        return repository.getPropertyById(propertyId)
    }
}