package com.propertymanager.domain.usecase.location.state

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteStateUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(countryId: Int, stateId: Int): Result<Unit> =
        repository.deleteState(countryId, stateId)
}
