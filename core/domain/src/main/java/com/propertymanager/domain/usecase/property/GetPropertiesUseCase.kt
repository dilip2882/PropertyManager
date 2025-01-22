package com.propertymanager.domain.usecase.property

import com.propertymanager.domain.model.Property
import com.propertymanager.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPropertiesUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(): Flow<List<Property>> = repository.getProperties()
}
