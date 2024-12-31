package com.propertymanager.domain.usecase.property

import com.propertymanager.domain.model.Property
import com.propertymanager.domain.repository.PropertyRepository
import javax.inject.Inject

class UpdateAddressUseCase @Inject constructor(private val repository: PropertyRepository) {
    suspend operator fun invoke(propertyId: String, address: Property.Address) =
        repository.updateAddress(propertyId, address)
}
