package com.propertymanager.domain.usecase.location.state

import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStatesForCountryUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(countryId: Int): Flow<List<State>> =
        repository.getStatesForCountry(countryId)
}
