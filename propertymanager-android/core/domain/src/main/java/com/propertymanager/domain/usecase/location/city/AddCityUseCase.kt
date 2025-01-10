package com.propertymanager.domain.usecase.location.city

import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class AddCityUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(city: City): Result<Unit> {
        return repository.addCity(city)
    }
}
