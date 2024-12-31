package com.propertymanager.domain.usecase.location

import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCitiesForStateUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(stateId: Int): Flow<List<City>> =
        repository.getCitiesForState(stateId)
}

