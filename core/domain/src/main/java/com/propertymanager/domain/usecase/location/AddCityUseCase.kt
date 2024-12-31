package com.propertymanager.domain.usecase.location

import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class AddCityUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(stateId: Int, city: City): Result<Unit> =
        repository.addCity(stateId, city)
}
