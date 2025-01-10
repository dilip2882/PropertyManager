package com.propertymanager.domain.usecase.location.state

import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateStateUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(state: State): Result<Unit> {
        return repository.updateState(state)
    }
}
