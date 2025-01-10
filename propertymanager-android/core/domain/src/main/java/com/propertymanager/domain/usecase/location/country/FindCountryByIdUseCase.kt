package com.propertymanager.domain.usecase.location.country

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class FindCountryByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(countryId: Int) =
        repository.findCountryById(countryId)
}
