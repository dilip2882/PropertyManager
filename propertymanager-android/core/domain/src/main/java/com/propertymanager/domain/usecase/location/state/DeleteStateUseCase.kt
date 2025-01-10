package com.propertymanager.domain.usecase.location.state

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteStateUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(stateId: Int): Result<Unit> {
        return repository.deleteState(stateId)
    }
}
