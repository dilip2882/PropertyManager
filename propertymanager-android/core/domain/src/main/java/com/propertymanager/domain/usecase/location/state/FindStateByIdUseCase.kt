package com.propertymanager.domain.usecase.location.state

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class FindStateByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(stateId: Int) =
        repository.findStateById(stateId)
}
