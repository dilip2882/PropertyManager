package com.propertymanager.domain.usecase.location

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteCountryUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(countryId: Int): Result<Unit> =
        repository.deleteCountry(countryId)
}
