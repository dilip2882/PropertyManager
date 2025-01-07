package com.propertymanager.domain.usecase.location.city

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class FindCityByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(cityId: Int) =
        repository.findCityById(cityId)
}
