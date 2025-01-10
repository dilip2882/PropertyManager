package com.propertymanager.domain.usecase.location.country

import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(): Flow<List<Country>> =
        repository.getCountries()
}
