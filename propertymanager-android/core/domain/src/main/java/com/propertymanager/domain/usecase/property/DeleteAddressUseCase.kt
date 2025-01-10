package com.propertymanager.domain.usecase.property

import com.propertymanager.domain.repository.PropertyRepository
import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(private val repository: PropertyRepository) {
    suspend operator fun invoke(propertyId: String) =
        repository.deleteAddress(propertyId)
}
