package com.propertymanager.domain.usecase.location

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteCityUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(stateId: Int, cityId: Int): Result<Unit> =
        repository.deleteCity(stateId, cityId)
}
