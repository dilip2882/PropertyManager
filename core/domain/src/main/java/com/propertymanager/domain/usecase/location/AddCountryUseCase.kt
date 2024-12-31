package com.propertymanager.domain.usecase.location

import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class AddCountryUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(country: Country): Result<Unit> =
        repository.addCountry(country)
}
