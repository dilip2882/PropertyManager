package com.propertymanager.domain.usecase.property

import com.propertymanager.domain.model.Property
import com.propertymanager.domain.repository.PropertyRepository
import javax.inject.Inject

class AddPropertyUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(property: Property): String {
        return repository.addProperty(property)
    }
}
