package com.propertymanager.domain.usecase.location.country

import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateCountryUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(country: Country): Result<Unit> =
        repository.updateCountry(country)
}
