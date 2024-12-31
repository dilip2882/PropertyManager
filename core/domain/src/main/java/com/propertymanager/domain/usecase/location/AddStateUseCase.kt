package com.propertymanager.domain.usecase.location

import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class AddStateUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(countryId: Int, state: State): Result<Unit> =
        repository.addState(countryId, state)
}

