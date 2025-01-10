package com.propertymanager.domain.usecase.location.city

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteCityUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(cityId: Int): Result<Unit> {
        return repository.deleteCity(cityId)
    }
}
